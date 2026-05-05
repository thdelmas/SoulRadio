@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package com.soulradio.soulradio

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import java.util.Calendar

// Owns the AUTO hour-boundary tick so the loop advances even after the
// activity is destroyed (the radio is meant to be left on).
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    private val handler = Handler(Looper.getMainLooper())
    private var autoEnabled = false
    private var autoCurrent: Frequency? = null
    private var tickRunnable: Runnable? = null
    private var fadeRunnable: Runnable? = null

    private val schumannUnderlay = SchumannUnderlay()

    private val underlayListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateUnderlay()
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateUnderlay()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val renderersFactory = object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
            ): AudioSink {
                return DefaultAudioSink.Builder(context)
                    .setAudioProcessors(arrayOf<AudioProcessor>(schumannUnderlay))
                    .setEnableFloatOutput(enableFloatOutput)
                    .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                    .build()
            }
        }
        val player = ExoPlayer.Builder(this, renderersFactory)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(underlayListener)
            }
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_AUTO_ON -> enableAuto()
            ACTION_AUTO_OFF -> disableAuto()
            ACTION_PAUSE_FOR_RADIO -> pauseForRadio()
            ACTION_RESUME_FROM_RADIO -> resumeFromRadio()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // Swipe-away while paused stops the service; while playing keeps going
    // (the radio is meant to be left on).
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player != null && (!player.playWhenReady || player.mediaItemCount == 0)) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        cancelTick()
        cancelFade()
        mediaSession?.run {
            player.removeListener(underlayListener)
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    // Tracks what's actually audible (not what AUTO thinks should be):
    // hooked to player events so the underlay only rides 7.83 when 7.83
    // is the playing item.
    private fun updateUnderlay() {
        val player = mediaSession?.player ?: return
        val key = bandKeyOf(player.currentMediaItem)
        val playing = player.isPlaying
        schumannUnderlay.setEnabled(playing && key == NIGHT_BAND_KEY)
    }

    private fun bandKeyOf(item: MediaItem?): String? {
        val uri = item?.localConfiguration?.uri?.toString() ?: return null
        return Regex("^asset:///audio/([^/]+)/.+$").find(uri)?.groupValues?.get(1)
    }

    private fun enableAuto() {
        autoEnabled = true
        cancelTick()
        applyAutoForNow()
        scheduleNextTick()
    }

    private fun disableAuto() {
        autoEnabled = false
        cancelTick()
        // Cancel only our fade — TrackEngine handles the playback change.
        cancelFade()
    }

    private fun pauseForRadio() {
        cancelTick()
        cancelFade()
        val player = mediaSession?.player ?: return
        if (player.isPlaying || player.playWhenReady) {
            fadeTo(player, 0f) { player.pause() }
        }
    }

    // AUTO resumes if it was on; tapped tones don't auto-resume — the user
    // can re-tap if they want the room back.
    private fun resumeFromRadio() {
        val autoOn = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(PREF_AUTO_ENABLED, false)
        if (!autoOn) return
        if (!autoEnabled) {
            enableAuto()
        } else {
            applyAutoForNow()
            scheduleNextTick()
        }
    }

    private fun scheduleNextTick() {
        val r = object : Runnable {
            override fun run() {
                if (!autoEnabled) return
                applyAutoForNow()
                handler.postDelayed(this, msUntilNextBoundary())
            }
        }
        tickRunnable = r
        handler.postDelayed(r, msUntilNextBoundary())
    }

    private fun cancelTick() {
        tickRunnable?.let { handler.removeCallbacks(it) }
        tickRunnable = null
    }

    private fun applyAutoForNow() {
        val target = Frequencies.forNow(this)
        val player = mediaSession?.player ?: return
        if (autoCurrent?.key == target.key && player.isPlaying) return
        switchTo(target)
    }

    private fun switchTo(freq: Frequency) {
        val player = mediaSession?.player ?: return
        val uris = TrackResolver.urisFor(this, freq)
        if (uris.isEmpty()) {
            cancelFade()
            fadeTo(player, 0f) { player.pause() }
            autoCurrent = null
            return
        }
        cancelFade()
        if (!player.isPlaying) {
            applyMedia(player, uris)
            autoCurrent = freq
            player.volume = 0f
            player.play()
            fadeTo(player, TARGET_VOLUME) {}
        } else {
            // fade-out → swap → fade-in (manifesto: no hard cuts).
            fadeTo(player, 0f) {
                applyMedia(player, uris)
                autoCurrent = freq
                player.play()
                fadeTo(player, TARGET_VOLUME) {}
            }
        }
    }

    private fun applyMedia(player: Player, uris: List<String>) {
        val items = uris.map { MediaItem.fromUri(it) }
        player.setMediaItems(items)
        player.shuffleModeEnabled = items.size > 1
        player.repeatMode = if (items.size > 1) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_ONE
        player.prepare()
    }

    private fun fadeTo(player: Player, target: Float, onDone: () -> Unit) {
        val start = player.volume
        val steps = (FADE_MS / FADE_STEP_MS).toInt().coerceAtLeast(1)
        var step = 0
        val r = object : Runnable {
            override fun run() {
                val live = mediaSession?.player ?: return
                step++
                val t = step.toFloat() / steps
                live.volume = start + (target - start) * t
                if (step < steps) {
                    handler.postDelayed(this, FADE_STEP_MS)
                } else {
                    live.volume = target
                    fadeRunnable = null
                    onDone()
                }
            }
        }
        fadeRunnable = r
        handler.postDelayed(r, FADE_STEP_MS)
    }

    private fun cancelFade() {
        fadeRunnable?.let { handler.removeCallbacks(it) }
        fadeRunnable = null
    }

    private fun msUntilNextBoundary(): Long {
        val loc = LocationStore.get(this) ?: return msUntilNextClockHour()
        val now = Calendar.getInstance()
        val today = SolarCalculator.compute(
            now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH),
            loc, now.timeZone,
        ) ?: return msUntilNextClockHour()
        val tomorrow = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
        val tomorrowSun = SolarCalculator.compute(
            tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH) + 1,
            tomorrow.get(Calendar.DAY_OF_MONTH), loc, tomorrow.timeZone,
        )
        val nextBoundary = SolarSchedule.nextBoundaryMillis(
            now.timeInMillis, today, tomorrowSun?.sunriseUtcMillis,
        )
        return (nextBoundary - now.timeInMillis).coerceAtLeast(1_000L)
    }

    private fun msUntilNextClockHour(): Long {
        val cal = Calendar.getInstance()
        val ms = (60 - cal.get(Calendar.MINUTE)) * 60_000L -
            cal.get(Calendar.SECOND) * 1000L -
            cal.get(Calendar.MILLISECOND)
        return ms.coerceAtLeast(1_000L)
    }

    companion object {
        const val ACTION_AUTO_ON = "com.soulradio.action.AUTO_ON"
        const val ACTION_AUTO_OFF = "com.soulradio.action.AUTO_OFF"
        const val ACTION_PAUSE_FOR_RADIO = "com.soulradio.action.PAUSE_FOR_RADIO"
        const val ACTION_RESUME_FROM_RADIO = "com.soulradio.action.RESUME_FROM_RADIO"

        private const val PREFS = "soulradio.state"
        private const val PREF_AUTO_ENABLED = "auto_enabled"

        private const val TARGET_VOLUME = 0.7f
        private const val FADE_MS = 1500L
        private const val FADE_STEP_MS = 30L

        private const val NIGHT_BAND_KEY = "7.83"

        fun isAutoEnabled(context: Context): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(PREF_AUTO_ENABLED, false)

        // Manifesto §"the promise": "Just leave it on." First launch defaults to AUTO.
        fun startIfFirstLaunch(context: Context) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            if (!prefs.contains(PREF_AUTO_ENABLED)) setAuto(context, true)
        }

        fun setAuto(context: Context, enabled: Boolean) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            if (prefs.contains(PREF_AUTO_ENABLED) &&
                prefs.getBoolean(PREF_AUTO_ENABLED, false) == enabled) {
                return
            }
            prefs.edit().putBoolean(PREF_AUTO_ENABLED, enabled).apply()
            val intent = Intent(context, PlaybackService::class.java).apply {
                action = if (enabled) ACTION_AUTO_ON else ACTION_AUTO_OFF
            }
            context.startService(intent)
        }

        // Does NOT write to AUTO pref — the user's choice is preserved.
        fun pauseForRadio(context: Context) {
            val intent = Intent(context, PlaybackService::class.java).apply {
                action = ACTION_PAUSE_FOR_RADIO
            }
            context.startService(intent)
        }

        fun resumeFromRadio(context: Context) {
            val intent = Intent(context, PlaybackService::class.java).apply {
                action = ACTION_RESUME_FROM_RADIO
            }
            context.startService(intent)
        }
    }
}

@Composable
internal fun PauseDialWhileOpen() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        PlaybackService.pauseForRadio(context)
        onDispose { PlaybackService.resumeFromRadio(context) }
    }
}
