package com.soulradio.soulradio

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

// Activity-side controller surface for the dial. Reads playback state via
// MediaController; routes band-switch requests through PlaybackService so
// the service's two-player crossfade is the only path that changes the
// audible band (manifesto §6 — crossfades that do not jolt).
class TrackEngine(private val context: Context) {

    private val controllerFuture: ListenableFuture<MediaController>
    private var controller: MediaController? = null

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
        }, ContextCompat.getMainExecutor(context))
    }

    fun isTuned(freq: Frequency): Boolean = freq.key in tunedKeys

    fun selectFrequency(freq: Frequency?) {
        if (freq == null) {
            PlaybackService.stopDial(context)
        } else {
            PlaybackService.playBand(context, freq.key)
        }
    }

    fun release() {
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

    // The asset URI regex remains here for NowPlaying lookup (curated
    // tracks only — user files have no NowPlaying entry).
    private fun frequencyFromMediaItem(item: MediaItem?): Frequency? {
        val key = TrackResolver.bandKeyOf(item) ?: return null
        return Frequencies.byKey(key)
    }

    private fun trackFromMediaItem(item: MediaItem?): NowPlaying? {
        val key = TrackResolver.bandKeyOf(item) ?: return null
        val asset = assetNameOf(item) ?: return null
        val freq = Frequencies.byKey(key) ?: return null
        return Frequencies.trackByAsset(freq, asset)
    }

    private fun assetNameOf(item: MediaItem?): String? {
        val uri = item?.localConfiguration?.uri?.toString() ?: return null
        return Regex("^asset:///audio/[^/]+/(.+)$").find(uri)?.groupValues?.get(1)
    }
}
