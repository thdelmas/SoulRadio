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
// activity is destroyed (the radio is meant to be left on). Both AUTO
// and the dial route through switchTo so the two-player crossfade is
// the only path that changes audible band.
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    private val handler = Handler(Looper.getMainLooper())
    private var autoEnabled = false
    private var autoCurrent: Frequency? = null
    private var tickRunnable: Runnable? = null
    private var fadeRunnable: Runnable? = null

    // Two ExoPlayers alternate roles. The one bound to mediaSession is
    // "active"; the other is "standby". Band switches and track rotations
    // both load on standby at vol 0, crossfade in lockstep, then promote
    // standby. Manifesto §6 — crossfades that do not jolt.
    private var playerA: ExoPlayer? = null
    private var playerB: ExoPlayer? = null
    private val underlayA = SchumannUnderlay()
    private val underlayB = SchumannUnderlay()

    // Multi-track bands carry one MediaItem at a time with REPEAT_MODE_OFF;
    // rotateRunnable polls position and fires the standby preload +
    // crossfade ROTATE_LEAD_MS before end-of-stream.
    private var bandQueue: List<String> = emptyList()
    private var bandIndex: Int = -1
    private var bandFreq: Frequency? = null
    private var rotateRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        playerA = buildPlayer(underlayA).also { it.addListener(underlayListener(it, underlayA)) }
        playerB = buildPlayer(underlayB).also { it.addListener(underlayListener(it, underlayB)) }
        mediaSession = MediaSession.Builder(this, playerA!!).build()
    }

    private fun buildPlayer(underlay: SchumannUnderlay): ExoPlayer {
        val renderersFactory = object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
            ): AudioSink {
                return DefaultAudioSink.Builder(context)
                    .setAudioProcessors(arrayOf<AudioProcessor>(underlay))
                    .setEnableFloatOutput(enableFloatOutput)
                    .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                    .build()
            }
        }
        return ExoPlayer.Builder(this, renderersFactory)
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
            .apply { repeatMode = Player.REPEAT_MODE_ONE }
    }

    private fun underlayListener(player: ExoPlayer, underlay: SchumannUnderlay) =
        object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                refreshUnderlay(player, underlay)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                refreshUnderlay(player, underlay)
            }
        }

    private fun refreshUnderlay(p: ExoPlayer, u: SchumannUnderlay) {
        val key = TrackResolver.bandKeyOf(p.currentMediaItem)
        u.setEnabled(p.isPlaying && key == NIGHT_BAND_KEY)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_AUTO_ON -> enableAuto()
            ACTION_AUTO_OFF -> disableAuto()
            ACTION_PAUSE_FOR_RADIO -> pauseForRadio()
            ACTION_RESUME_FROM_RADIO -> resumeFromRadio()
            ACTION_PLAY_BAND -> {
                val key = intent.getStringExtra(EXTRA_BAND_KEY)
                Frequencies.byKey(key.orEmpty())?.let { switchTo(it, fromAuto = false) }
            }
            ACTION_STOP_DIAL -> stopDial()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // Swipe-away while paused stops the service; while playing keeps going
    // (the radio is meant to be left on).
    override fun onTaskRemoved(rootIntent: Intent?) {
        val active = activePlayer()
        if (active != null && (!active.playWhenReady || active.mediaItemCount == 0)) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        cancelTick()
        cancelFade()
        cancelRotateWatch()
        mediaSession?.release()
        playerA?.release()
        playerB?.release()
        playerA = null
        playerB = null
        mediaSession = null
        super.onDestroy()
    }

    private fun activePlayer(): ExoPlayer? = mediaSession?.player as? ExoPlayer

    private fun standbyPlayer(): ExoPlayer? {
        val a = playerA ?: return null
        val b = playerB ?: return null
        return if (mediaSession?.player === a) b else a
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
        cancelInFlightCrossfade()
    }

    private fun pauseForRadio() {
        cancelTick()
        cancelRotateWatch()
        cancelInFlightCrossfade()
        val active = activePlayer() ?: return
        if (active.isPlaying || active.playWhenReady) {
            fadeTo(active, 0f) { active.pause() }
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

    private fun stopDial() {
        cancelRotateWatch()
        cancelInFlightCrossfade()
        val active = activePlayer() ?: return
        fadeTo(active, 0f) { active.pause() }
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
        val active = activePlayer() ?: return
        if (autoCurrent?.key == target.key && active.isPlaying) return
        switchTo(target, fromAuto = true)
    }

    private fun switchTo(freq: Frequency, fromAuto: Boolean) {
        val active = activePlayer() ?: return
        val standby = standbyPlayer() ?: return
        if (active.isPlaying && TrackResolver.bandKeyOf(active.currentMediaItem) == freq.key) {
            if (fromAuto) autoCurrent = freq
            return
        }
        val uris = TrackResolver.urisFor(this, freq)

        if (uris.isEmpty()) {
            cancelRotateWatch()
            cancelInFlightCrossfade()
            fadeTo(active, 0f) { active.pause() }
            if (fromAuto) autoCurrent = null
            bandFreq = null
            bandQueue = emptyList()
            bandIndex = -1
            return
        }

        cancelRotateWatch()
        cancelInFlightCrossfade()

        val startIndex = if (uris.size > 1) uris.indices.random() else 0
        bandQueue = uris
        bandIndex = startIndex
        bandFreq = freq

        if (!active.isPlaying) {
            loadTrack(active, freq, uris[startIndex])
            if (fromAuto) autoCurrent = freq
            active.volume = 0f
            active.play()
            fadeTo(active, TARGET_VOLUME) { startRotateWatch() }
            return
        }

        loadTrack(standby, freq, uris[startIndex])
        if (fromAuto) autoCurrent = freq
        standby.volume = 0f
        standby.play()
        crossfade(
            from = active,
            to = standby,
            fromStart = active.volume,
            onDone = {
                mediaSession?.player = standby
                active.pause()
                active.clearMediaItems()
                startRotateWatch()
            },
        )
    }

    private fun loadTrack(player: Player, freq: Frequency, uri: String) {
        val item = MediaItem.Builder().setUri(uri).setMediaId(freq.key).build()
        player.setMediaItems(listOf(item), 0, 0L)
        player.shuffleModeEnabled = false
        // Multi-track bands stop at end-of-stream so the rotation watcher
        // can fade to the next file; single-track bands self-loop.
        player.repeatMode =
            if (bandQueue.size > 1) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE
        player.prepare()
    }

    private fun startRotateWatch() {
        cancelRotateWatch()
        if (bandQueue.size <= 1) return
        val r = object : Runnable {
            override fun run() {
                val active = activePlayer()
                val freq = bandFreq
                if (active == null || freq == null || mediaSession == null ||
                    bandQueue.size <= 1) {
                    rotateRunnable = null
                    return
                }
                if (!active.isPlaying || fadeRunnable != null) {
                    handler.postDelayed(this, ROTATE_TICK_MS)
                    return
                }
                val dur = active.duration
                val pos = active.currentPosition
                if (dur != C.TIME_UNSET && dur - pos <= FADE_MS + ROTATE_LEAD_MS) {
                    rotateRunnable = null
                    rotateToNextTrack(freq)
                } else {
                    handler.postDelayed(this, ROTATE_TICK_MS)
                }
            }
        }
        rotateRunnable = r
        handler.postDelayed(r, ROTATE_TICK_MS)
    }

    private fun cancelRotateWatch() {
        rotateRunnable?.let { handler.removeCallbacks(it) }
        rotateRunnable = null
    }

    private fun rotateToNextTrack(freq: Frequency) {
        val active = activePlayer() ?: return
        val standby = standbyPlayer() ?: return
        val nextIdx = nextBandIndex()
        bandIndex = nextIdx
        loadTrack(standby, freq, bandQueue[nextIdx])
        standby.volume = 0f
        standby.play()
        crossfade(
            from = active,
            to = standby,
            fromStart = active.volume,
            onDone = {
                mediaSession?.player = standby
                active.pause()
                active.clearMediaItems()
                startRotateWatch()
            },
        )
    }

    private fun nextBandIndex(): Int {
        val size = bandQueue.size
        if (size <= 1) return 0
        var n = (0 until size).random()
        if (n == bandIndex) n = (n + 1) % size
        return n
    }

    private fun fadeTo(player: Player, target: Float, onDone: () -> Unit) {
        val start = player.volume
        ramp({ t -> player.volume = start + (target - start) * t }, onDone)
    }

    // 'from' fades fromStart→0, 'to' fades 0→TARGET_VOLUME, in lockstep.
    // Both players are audible across the fade window so the listener
    // hears the new piece rising before the old one is gone.
    private fun crossfade(from: Player, to: Player, fromStart: Float, onDone: () -> Unit) {
        ramp({ t ->
            from.volume = fromStart * (1f - t)
            to.volume = TARGET_VOLUME * t
        }, onDone)
    }

    private fun ramp(apply: (t: Float) -> Unit, onDone: () -> Unit) {
        val steps = (FADE_MS / FADE_STEP_MS).toInt().coerceAtLeast(1)
        var step = 0
        val r = object : Runnable {
            override fun run() {
                if (mediaSession == null) return
                step++
                val t = step.toFloat() / steps
                apply(t)
                if (step < steps) {
                    handler.postDelayed(this, FADE_STEP_MS)
                } else {
                    apply(1f)
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

    // Cancels any ramp in flight AND silences standby if a crossfade was
    // mid-way — otherwise standby keeps playing under whatever the next
    // caller does to active.
    private fun cancelInFlightCrossfade() {
        cancelFade()
        val standby = standbyPlayer() ?: return
        if (standby.isPlaying || standby.playWhenReady || standby.mediaItemCount > 0) {
            standby.volume = 0f
            standby.pause()
            standby.clearMediaItems()
        }
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
        const val ACTION_PLAY_BAND = "com.soulradio.action.PLAY_BAND"
        const val ACTION_STOP_DIAL = "com.soulradio.action.STOP_DIAL"
        const val EXTRA_BAND_KEY = "band_key"

        private const val PREFS = "soulradio.state"
        private const val PREF_AUTO_ENABLED = "auto_enabled"

        private const val TARGET_VOLUME = 0.7f
        private const val FADE_MS = 1500L
        private const val FADE_STEP_MS = 30L
        private const val ROTATE_TICK_MS = 250L
        // Headroom over FADE_MS so handler jitter can't starve the tail.
        private const val ROTATE_LEAD_MS = 500L

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
            fire(context, if (enabled) ACTION_AUTO_ON else ACTION_AUTO_OFF)
        }

        // Does NOT write to AUTO pref — the user's choice is preserved.
        fun pauseForRadio(context: Context) = fire(context, ACTION_PAUSE_FOR_RADIO)
        fun resumeFromRadio(context: Context) = fire(context, ACTION_RESUME_FROM_RADIO)
        fun playBand(context: Context, key: String) =
            fire(context, ACTION_PLAY_BAND) { putExtra(EXTRA_BAND_KEY, key) }
        fun stopDial(context: Context) = fire(context, ACTION_STOP_DIAL)

        private fun fire(
            context: Context,
            action: String,
            extras: Intent.() -> Unit = {},
        ) {
            val intent = Intent(context, PlaybackService::class.java)
                .also { it.action = action }
                .apply(extras)
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
