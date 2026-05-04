package com.soulradio.soulradio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

/**
 * Standalone sine-tone demo for Radio mode rows. Builds a one-off
 * [AudioTrack], synthesises a steady tone at the requested Hz on a
 * background thread, and tears down cleanly on stop. Click-free start and
 * stop via short linear envelopes (~120 ms).
 *
 * **Manifesto framing.** Bare sines are not the radio's product (the
 * curated dial recordings are). This class exists *only* to give the
 * Radio mode catalogue an honest demo of "here is what 528 Hz sounds
 * like as a tone" — opt-in, deliberate, never autoplay, never on the
 * default surface. The dial and auto loop must not use this path. See
 * [MANIFESTO.md](../../../../../../MANIFESTO.md): "We will not let the
 * Radio bleed into the room. Exploration is opt-in."
 *
 * Sub-audible Hz (< 20) are silently rejected — at those rates an
 * AudioTrack writes near-silence, and pretending to "play" them would
 * misrepresent what the listener is hearing. The Radio UI keeps the
 * entry-expand affordance for those rows; only the audio is gated.
 */
class SineDemo {

    @Volatile private var thread: ToneThread? = null

    /**
     * Start (or re-start) playing a sine at [hz]. If a tone is already
     * playing, it is faded out and replaced. Sub-audible requests are
     * a no-op and any current tone is stopped.
     */
    @Synchronized
    fun start(hz: Double) {
        stopInternal()
        if (hz < MIN_AUDIBLE_HZ) return
        thread = ToneThread(hz).also { it.start() }
    }

    @Synchronized
    fun stop() = stopInternal()

    @Synchronized
    fun release() = stopInternal()

    private fun stopInternal() {
        val t = thread ?: return
        t.requestStop()
        // Bound the wait so a pathological case (driver hang) cannot
        // freeze the UI; the tone thread releases the AudioTrack in
        // its own finally block, so an orphaned thread cleans up after
        // itself even if join() times out.
        t.join(JOIN_TIMEOUT_MS)
        thread = null
    }

    /**
     * Worker thread. Owns its [AudioTrack] for its entire lifetime,
     * builds → plays fade-in + steady → on stop, plays fade-out →
     * releases. Synthesis is mono PCM 16-bit at 44.1 kHz so no resample
     * happens in the audio chain.
     */
    private class ToneThread(private val hz: Double) : Thread("SineDemo-$hz") {

        @Volatile private var stopRequested = false

        fun requestStop() { stopRequested = true }

        override fun run() {
            val track = buildTrack()
            try {
                track.play()
                playSteady(track)
                playFadeOut(track)
                track.stop()
            } catch (_: IllegalStateException) {
                // Track was released or in an unexpected state — bail
                // quietly; outer finally still releases.
            } finally {
                track.release()
            }
        }

        private fun buildTrack(): AudioTrack {
            val minBytes = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            )
            // 4× minimum gives the writer enough slack to refill without
            // underrunning at low priority.
            val bufferBytes = (minBytes * 4).coerceAtLeast(minBytes)
            return AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                )
                .setBufferSizeInBytes(bufferBytes)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        }

        private var phase: Double = 0.0
        private val phaseIncrement: Double = 2.0 * PI * hz / SAMPLE_RATE

        private fun playSteady(track: AudioTrack) {
            val buffer = ShortArray(BUFFER_SAMPLES)
            val fadeInSamples = (FADE_SECONDS * SAMPLE_RATE).toInt()
            var samples = 0
            while (!stopRequested) {
                for (i in buffer.indices) {
                    val envelope = if (samples < fadeInSamples) {
                        samples.toFloat() / fadeInSamples
                    } else 1f
                    buffer[i] = nextSample(envelope)
                    samples++
                }
                val written = track.write(buffer, 0, buffer.size)
                if (written < 0) return
            }
        }

        private fun playFadeOut(track: AudioTrack) {
            val fadeOutSamples = (FADE_SECONDS * SAMPLE_RATE).toInt()
            val buffer = ShortArray(fadeOutSamples)
            for (i in buffer.indices) {
                val envelope = 1f - i.toFloat() / fadeOutSamples
                buffer[i] = nextSample(envelope)
            }
            track.write(buffer, 0, buffer.size)
        }

        private fun nextSample(envelope: Float): Short {
            val s = sin(phase) * AMPLITUDE * envelope
            phase += phaseIncrement
            if (phase >= TWO_PI) phase -= TWO_PI
            return (s * Short.MAX_VALUE).toInt().toShort()
        }

        companion object {
            private const val TWO_PI: Double = 2.0 * PI
        }
    }

    companion object {
        /** Below this, AudioTrack output is effectively silence. */
        const val MIN_AUDIBLE_HZ: Double = 20.0

        private const val SAMPLE_RATE = 44_100
        private const val BUFFER_SAMPLES = 1_024
        private const val FADE_SECONDS: Float = 0.12f
        // -15 dB FS roughly. Quiet enough to not startle on a kitchen
        // speaker, audible on phone hardware. Tune on a real device.
        private const val AMPLITUDE: Double = 0.18

        private const val JOIN_TIMEOUT_MS: Long = 800
    }
}
