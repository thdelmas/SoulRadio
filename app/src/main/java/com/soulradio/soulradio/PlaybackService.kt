package com.soulradio.soulradio

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import java.util.Calendar

/**
 * Hosts the ExoPlayer + MediaSession so audio survives screen lock and
 * surfaces lockscreen / Bluetooth controls. Also owns the 24-hour AUTO
 * schedule: when AUTO is on, the service swaps the playing track at
 * each hour boundary — so the loop keeps advancing overnight even
 * after the activity is destroyed.
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    private val handler = Handler(Looper.getMainLooper())
    private var autoEnabled = false
    private var autoCurrent: Frequency? = null
    private var tickRunnable: Runnable? = null
    private var fadeRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
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
            }
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_AUTO_ON -> enableAuto()
            ACTION_AUTO_OFF -> disableAuto()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        // If user swipes the app away while paused, stop the service.
        // While playing, keep going (the radio is meant to be left on).
        val player = mediaSession?.player
        if (player != null && (!player.playWhenReady || player.mediaItemCount == 0)) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        cancelTick()
        cancelFade()
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
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
        // Don't touch playback here — the activity (TrackEngine) will fade
        // out or swap to a user-tapped tone. Cancelling our fade lets it.
        cancelFade()
    }

    private fun scheduleNextTick() {
        val r = object : Runnable {
            override fun run() {
                if (!autoEnabled) return
                applyAutoForNow()
                handler.postDelayed(this, msUntilNextHour())
            }
        }
        tickRunnable = r
        handler.postDelayed(r, msUntilNextHour())
    }

    private fun cancelTick() {
        tickRunnable?.let { handler.removeCallbacks(it) }
        tickRunnable = null
    }

    private fun applyAutoForNow() {
        val target = Frequencies.forNow()
        val player = mediaSession?.player ?: return
        if (autoCurrent?.key == target.key && player.isPlaying) return
        switchTo(target)
    }

    private fun switchTo(freq: Frequency) {
        val player = mediaSession?.player ?: return
        val asset = firstAssetIn(freq.assetFolder) ?: run {
            cancelFade()
            fadeTo(player, 0f) { player.pause() }
            autoCurrent = null
            return
        }
        cancelFade()
        if (!player.isPlaying) {
            applyMedia(player, asset)
            autoCurrent = freq
            player.volume = 0f
            player.play()
            fadeTo(player, TARGET_VOLUME) {}
        } else {
            // fade-out → swap → fade-in (single player, but no hard cuts).
            fadeTo(player, 0f) {
                applyMedia(player, asset)
                autoCurrent = freq
                player.play()
                fadeTo(player, TARGET_VOLUME) {}
            }
        }
    }

    private fun applyMedia(player: Player, assetPath: String) {
        player.setMediaItem(MediaItem.fromUri("asset:///$assetPath"))
        player.repeatMode = Player.REPEAT_MODE_ONE
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

    private fun firstAssetIn(folder: String): String? = try {
        assets.list(folder)
            ?.firstOrNull { !it.startsWith(".") }
            ?.let { "$folder/$it" }
    } catch (_: Exception) {
        null
    }

    private fun msUntilNextHour(): Long {
        val cal = Calendar.getInstance()
        val ms = (60 - cal.get(Calendar.MINUTE)) * 60_000L -
            cal.get(Calendar.SECOND) * 1000L -
            cal.get(Calendar.MILLISECOND)
        return ms.coerceAtLeast(1_000L)
    }

    companion object {
        const val ACTION_AUTO_ON = "com.soulradio.action.AUTO_ON"
        const val ACTION_AUTO_OFF = "com.soulradio.action.AUTO_OFF"

        private const val PREFS = "soulradio.state"
        private const val PREF_AUTO_ENABLED = "auto_enabled"

        private const val TARGET_VOLUME = 0.7f
        private const val FADE_MS = 1500L
        private const val FADE_STEP_MS = 30L

        fun isAutoEnabled(context: Context): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(PREF_AUTO_ENABLED, false)

        // Manifesto §"the promise" ends with "Tune in. Or better — don't.
        // Just leave it on." So on the very first launch (no stored pref),
        // start AUTO. The user can always toggle off; we then respect it.
        fun startIfFirstLaunch(context: Context) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            if (!prefs.contains(PREF_AUTO_ENABLED)) setAuto(context, true)
        }

        fun setAuto(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_AUTO_ENABLED, enabled)
                .apply()
            val intent = Intent(context, PlaybackService::class.java).apply {
                action = if (enabled) ACTION_AUTO_ON else ACTION_AUTO_OFF
            }
            context.startService(intent)
        }
    }
}
