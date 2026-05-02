package com.soulradio.soulradio

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

/**
 * Frequency-aware playback wrapper over a MediaController bound to
 * PlaybackService. The service owns the single ExoPlayer (so playback
 * survives screen lock and surfaces lockscreen / Bluetooth controls);
 * this class chooses what plays and rides the volume ramp for fades.
 *
 * Asset convention: the first non-hidden file inside `audio/<freq.key>/`
 * is the recording for that frequency. Frequencies with no bundled file
 * are "untuned" — selectable in the UI but no audio plays.
 */
class TrackEngine(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private val controllerFuture: ListenableFuture<MediaController>
    private var controller: MediaController? = null

    private var fadeRunnable: Runnable? = null
    private val targetVolume = 0.7f
    private val fadeStepMs = 30L
    private val fadeMs = 1500L

    private var pending: Frequency? = null
    private var current: Frequency? = null

    /** Frequencies whose asset folder contains a bundled recording. */
    val tunedKeys: Set<String> = Frequencies.all
        .filter { firstAssetIn(it.assetFolder) != null }
        .map { it.key }
        .toSet()

    init {
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()
            pending?.let { selectFrequency(it) }
        }, ContextCompat.getMainExecutor(context))
    }

    fun isTuned(freq: Frequency): Boolean = freq.key in tunedKeys

    /**
     * Switch playback to [freq]. Null or untuned frequencies fade the
     * current track out. If [freq] is already current, no-op.
     */
    fun selectFrequency(freq: Frequency?) {
        val c = controller
        if (c == null) {
            pending = freq
            return
        }
        pending = null

        if (freq == null || !isTuned(freq)) {
            cancelFade()
            fadeTo(c, 0f) { c.pause() }
            current = null
            return
        }
        if (current?.key == freq.key && c.isPlaying) return

        val asset = firstAssetIn(freq.assetFolder) ?: run {
            cancelFade()
            fadeTo(c, 0f) { c.pause() }
            current = null
            return
        }

        cancelFade()
        if (!c.isPlaying) {
            applyMedia(c, asset)
            current = freq
            c.volume = 0f
            c.play()
            fadeTo(c, targetVolume) {}
        } else {
            // fade-out → swap → fade-in. Not a true overlap (single player),
            // but keeps the manifesto's "no hard cuts" promise.
            fadeTo(c, 0f) {
                applyMedia(c, asset)
                current = freq
                c.play()
                fadeTo(c, targetVolume) {}
            }
        }
    }

    fun release() {
        cancelFade()
        MediaController.releaseFuture(controllerFuture)
        controller = null
    }

    private fun applyMedia(c: MediaController, assetPath: String) {
        c.setMediaItem(MediaItem.fromUri("asset:///$assetPath"))
        c.repeatMode = Player.REPEAT_MODE_ONE
        c.prepare()
    }

    private fun fadeTo(c: MediaController, target: Float, onDone: () -> Unit) {
        val start = c.volume
        val steps = (fadeMs / fadeStepMs).toInt().coerceAtLeast(1)
        var step = 0
        val r = object : Runnable {
            override fun run() {
                val live = controller ?: return
                step++
                val t = step.toFloat() / steps
                live.volume = start + (target - start) * t
                if (step < steps) {
                    handler.postDelayed(this, fadeStepMs)
                } else {
                    live.volume = target
                    fadeRunnable = null
                    onDone()
                }
            }
        }
        fadeRunnable = r
        handler.postDelayed(r, fadeStepMs)
    }

    private fun cancelFade() {
        fadeRunnable?.let { handler.removeCallbacks(it) }
        fadeRunnable = null
    }

    private fun firstAssetIn(folder: String): String? = try {
        context.assets.list(folder)
            ?.firstOrNull { !it.startsWith(".") }
            ?.let { "$folder/$it" }
    } catch (_: Exception) {
        null
    }
}
