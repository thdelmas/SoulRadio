package com.soulradio.soulradio

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

// Multi-track bands shuffle on REPEAT_MODE_ALL so each session rotates
// through different recordings; single-track bands use REPEAT_MODE_ONE
// for ExoPlayer's gapless seek-loop.
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

    private val _tunedKeys = mutableStateOf(TrackResolver.tunedKeys(context))
    val tunedKeys: Set<String> get() = _tunedKeys.value

    private val prefs = context.getSharedPreferences("soulradio.state", Context.MODE_PRIVATE)
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "library_source" || key == "user_tracks_json") {
            _tunedKeys.value = TrackResolver.tunedKeys(context)
        }
    }

    private val _currentFrequency = mutableStateOf<Frequency?>(null)
    val currentFrequency: State<Frequency?> = _currentFrequency

    private val _currentTrack = mutableStateOf<NowPlaying?>(null)
    val currentTrack: State<NowPlaying?> = _currentTrack

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            recomputeCurrentFrequency()
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            recomputeCurrentFrequency()
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            recomputeCurrentFrequency()
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()?.also { it.addListener(playerListener) }
            recomputeCurrentFrequency()
            pending?.let { selectFrequency(it) }
        }, ContextCompat.getMainExecutor(context))
    }

    fun isTuned(freq: Frequency): Boolean = freq.key in tunedKeys

    fun selectFrequency(freq: Frequency?) {
        val c = controller
        if (c == null) {
            pending = freq
            return
        }
        pending = null

        if (freq == null) {
            cancelFade()
            fadeTo(c, 0f) { c.pause() }
            current = null
            return
        }
        if (current?.key == freq.key && c.isPlaying) return

        // USER_ONLY may yield an empty list — fade out, let the loop roll on.
        val uris = TrackResolver.urisFor(context, freq)
        if (uris.isEmpty()) {
            cancelFade()
            fadeTo(c, 0f) { c.pause() }
            current = null
            return
        }

        cancelFade()
        if (!c.isPlaying) {
            applyMedia(c, freq, uris)
            current = freq
            c.volume = 0f
            c.play()
            fadeTo(c, targetVolume) {}
        } else {
            // fade-out → swap → fade-in (manifesto: no hard cuts).
            fadeTo(c, 0f) {
                applyMedia(c, freq, uris)
                current = freq
                c.play()
                fadeTo(c, targetVolume) {}
            }
        }
    }

    fun release() {
        cancelFade()
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        controller?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture)
        controller = null
    }

    private fun recomputeCurrentFrequency() {
        val c = controller
        val playing = c != null && c.isPlaying
        val item = c?.currentMediaItem
        _currentFrequency.value = if (playing) frequencyFromMediaItem(item) else null
        _currentTrack.value = if (playing) trackFromMediaItem(item) else null
    }

    // mediaId is stamped at queue-time with freq.key, so user-imported
    // content:// URIs identify the band correctly. The asset URI regex
    // remains for NowPlaying lookup (curated tracks only — user files
    // have no NowPlaying entry).
    private fun frequencyFromMediaItem(item: MediaItem?): Frequency? {
        val key = bandKeyOf(item) ?: return null
        return Frequencies.byKey(key)
    }

    private fun trackFromMediaItem(item: MediaItem?): NowPlaying? {
        val key = bandKeyOf(item) ?: return null
        val asset = assetNameOf(item) ?: return null
        val freq = Frequencies.byKey(key) ?: return null
        return Frequencies.trackByAsset(freq, asset)
    }

    private fun bandKeyOf(item: MediaItem?): String? {
        item ?: return null
        val stamped = item.mediaId.takeIf {
            it.isNotBlank() && it != MediaItem.DEFAULT_MEDIA_ID
        }
        if (stamped != null) return stamped
        val uri = item.localConfiguration?.uri?.toString() ?: return null
        return Regex("^asset:///audio/([^/]+)/.+$").find(uri)?.groupValues?.get(1)
    }

    private fun assetNameOf(item: MediaItem?): String? {
        val uri = item?.localConfiguration?.uri?.toString() ?: return null
        return Regex("^asset:///audio/[^/]+/(.+)$").find(uri)?.groupValues?.get(1)
    }

    private fun applyMedia(c: MediaController, freq: Frequency, uris: List<String>) {
        val items = uris.map {
            MediaItem.Builder().setUri(it).setMediaId(freq.key).build()
        }
        // Random start index so multi-track bands don't always open on
        // items[0]. shuffleModeEnabled only affects subsequent tracks.
        val startIndex = if (items.size > 1) items.indices.random() else 0
        c.setMediaItems(items, startIndex, 0L)
        c.shuffleModeEnabled = items.size > 1
        c.repeatMode = if (items.size > 1) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_ONE
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
}
