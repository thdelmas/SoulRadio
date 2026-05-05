package com.soulradio.soulradio

import android.content.ComponentName
import android.content.Context
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

/**
 * Frequency-aware playback wrapper over a MediaController bound to
 * PlaybackService. The service owns the single ExoPlayer (so playback
 * survives screen lock and surfaces lockscreen / Bluetooth controls);
 * this class chooses what plays and rides the volume ramp for fades.
 *
 * Each Frequency may bundle one or more recordings (see [Frequency.tracks]).
 * Multi-track bands are loaded as a shuffled playlist on REPEAT_MODE_ALL so
 * the order is different every session AND the radio rotates through the
 * pool while the user stays tuned — no more hearing the same take on loop
 * for an hour. Single-track bands stay on REPEAT_MODE_ONE for gapless loop.
 * Frequencies with zero tracks bundled are "untuned" — selectable in the UI
 * but no audio plays.
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

    /**
     * Frequencies that would yield a non-empty playlist under the active
     * [LibrarySource]. Computed on access so a fresh read picks up changes
     * the listener made on the Library screen — switching source, adding
     * or removing files. The Set is small (≤ 11 entries); the read is
     * cheap (SharedPreferences + a JSON parse).
     */
    val tunedKeys: Set<String>
        get() = TrackResolver.tunedKeys(context)

    private val _currentFrequency = mutableStateOf<Frequency?>(null)
    /**
     * The frequency actually playing in the service, derived from the
     * MediaController's current item and play state. Drives the dial
     * highlight so the UI stays honest after the activity is recreated
     * (audio kept playing in the service while we were gone) and so the
     * AUTO band update is instant when the schedule rolls over at the
     * hour boundary, instead of lagging behind a 60 s poll.
     */
    val currentFrequency: State<Frequency?> = _currentFrequency

    private val _currentTrack = mutableStateOf<NowPlaying?>(null)
    /**
     * The specific recording (work + performer) that is playing now,
     * resolved from the MediaController's current asset URI. The dial card
     * binds to this rather than to a static field on the Frequency, so
     * the now-playing label tracks the track that was actually picked
     * for this session.
     */
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
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()?.also { it.addListener(playerListener) }
            recomputeCurrentFrequency()
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

        if (freq == null) {
            cancelFade()
            fadeTo(c, 0f) { c.pause() }
            current = null
            return
        }
        if (current?.key == freq.key && c.isPlaying) return

        // Resolve the band's playlist via [TrackResolver] — curated, user
        // imports, or a mix, depending on the active LibrarySource. An
        // empty list under USER_ONLY (no user files filed on this band) is
        // valid; we fade out and let the loop's next tick roll on.
        val uris = TrackResolver.urisFor(context, freq)
        if (uris.isEmpty()) {
            cancelFade()
            fadeTo(c, 0f) { c.pause() }
            current = null
            return
        }

        cancelFade()
        if (!c.isPlaying) {
            applyMedia(c, uris)
            current = freq
            c.volume = 0f
            c.play()
            fadeTo(c, targetVolume) {}
        } else {
            // fade-out → swap → fade-in. Not a true overlap (single player),
            // but keeps the manifesto's "no hard cuts" promise.
            fadeTo(c, 0f) {
                applyMedia(c, uris)
                current = freq
                c.play()
                fadeTo(c, targetVolume) {}
            }
        }
    }

    fun release() {
        cancelFade()
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

    private fun frequencyFromMediaItem(item: MediaItem?): Frequency? {
        val key = keyAndAssetFromMediaItem(item)?.first ?: return null
        return Frequencies.byKey(key)
    }

    private fun trackFromMediaItem(item: MediaItem?): NowPlaying? {
        val (key, asset) = keyAndAssetFromMediaItem(item) ?: return null
        val freq = Frequencies.byKey(key) ?: return null
        return Frequencies.trackByAsset(freq, asset)
    }

    private fun keyAndAssetFromMediaItem(item: MediaItem?): Pair<String, String>? {
        val uri = item?.localConfiguration?.uri?.toString() ?: return null
        // Asset URIs are "asset:///audio/<key>/<filename>" — see assetFolder.
        val m = Regex("^asset:///audio/([^/]+)/(.+)$").find(uri) ?: return null
        return m.groupValues[1] to m.groupValues[2]
    }

    private fun applyMedia(c: MediaController, uris: List<String>) {
        val items = uris.map { MediaItem.fromUri(it) }
        // Multi-track: shuffle the pool and loop the playlist so we rotate
        // through different takes. shuffleModeEnabled only affects what comes
        // *next* — the playhead still starts on items[0] — so we also seed a
        // random start index, otherwise every session opens on the same
        // recording. Single-track: REPEAT_MODE_ONE keeps the gapless seek-loop
        // ExoPlayer does best.
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
