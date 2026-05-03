@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package com.soulradio.soulradio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.AudioProcessor.UnhandledAudioFormatException
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

/**
 * Real-time amplitude modulation at the Schumann fundamental (7.83 Hz),
 * inserted into the ExoPlayer audio chain. Multiplies each PCM frame by
 * `1 + depth · sin(2π · 7.83 · t)`. The modulation rate is below the
 * pitch threshold (~20 Hz), so the listener does not hear a tone — they
 * feel a slow cadence riding under whatever the carrier is playing.
 *
 * Gated by [setEnabled]: ramps depth 0 → target over [RAMP_SECONDS] when
 * engaged, target → 0 over the same when disengaged, so engagement has
 * no audible click.
 *
 * See FREQUENCIES.md § 7.83 Hz — *the Schumann resonance*. The depth is
 * conservative on purpose; the manifesto's "respect the ear" rules out
 * audible tremolo. Tune on a real device, not in a unit test.
 */
class SchumannUnderlay : BaseAudioProcessor() {

    @Volatile private var rampTarget: Float = 0f

    private var rampValue: Float = 0f
    private var rampPerSample: Float = 0f
    private var phase: Double = 0.0
    private var phaseIncrement: Double = 0.0

    fun setEnabled(enabled: Boolean) {
        rampTarget = if (enabled) DEPTH else 0f
    }

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw UnhandledAudioFormatException(inputAudioFormat)
        }
        phaseIncrement = 2.0 * Math.PI * FREQUENCY_HZ / inputAudioFormat.sampleRate
        // Ramp covers the full 0..DEPTH range in RAMP_SECONDS at constant rate.
        rampPerSample = DEPTH / (RAMP_SECONDS * inputAudioFormat.sampleRate)
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val byteCount = inputBuffer.remaining()
        if (byteCount == 0) return
        val out = replaceOutputBuffer(byteCount)
        modulate(
            input = inputBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(),
            output = out.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(),
            channels = inputAudioFormat.channelCount,
        )
        inputBuffer.position(inputBuffer.limit())
        out.position(byteCount)
        out.flip()
    }

    override fun onFlush() {
        // Don't reset rampValue: a flush mid-band (e.g. user re-enters the
        // 7.83 band right after leaving) shouldn't re-ramp from zero. Phase
        // is best left continuous too — the cadence is felt, not heard, so
        // exact phase across discontinuities is irrelevant.
    }

    /**
     * Pulled out so the math is unit-testable. Reads frames from [input],
     * writes modulated frames to [output]. Both buffers are positioned at
     * their start and processed to remaining(); on return, [output]'s
     * position is at the end of writes (caller is responsible for flip()).
     */
    internal fun modulate(
        input: java.nio.ShortBuffer,
        output: java.nio.ShortBuffer,
        channels: Int,
    ) {
        val frameCount = input.remaining() / channels
        for (f in 0 until frameCount) {
            // 1 + depth · sin(phase). At phase=0 the multiplier is 1 (pass-
            // through), so when rampValue is 0 the output equals the input
            // bit-for-bit modulo Short.coerceIn rounding.
            val envelope = 1f + rampValue * sin(phase).toFloat()
            for (c in 0 until channels) {
                val sample = input.get().toInt()
                val modulated = (sample * envelope).toInt().coerceIn(
                    Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt(),
                )
                output.put(modulated.toShort())
            }
            phase += phaseIncrement
            if (phase >= TWO_PI) phase -= TWO_PI
            rampValue = approach(rampValue, rampTarget, rampPerSample)
        }
    }

    internal fun rampValueForTest(): Float = rampValue
    internal fun setRampValueForTest(v: Float) { rampValue = v }
    internal fun setPhaseForTest(v: Double) { phase = v }
    internal fun configurePhaseIncrementForTest(sampleRate: Int) {
        phaseIncrement = 2.0 * Math.PI * FREQUENCY_HZ / sampleRate
        rampPerSample = DEPTH / (RAMP_SECONDS * sampleRate)
    }

    companion object {
        const val FREQUENCY_HZ: Double = 7.83

        // Modulation depth: ±DEPTH around unity gain. Kept small because
        // any AM at 7.83 Hz is technically audible as flutter; the goal is
        // for the body to register cadence without the ear naming a
        // tremolo. Real-device tuning trumps any value chosen here.
        const val DEPTH: Float = 0.04f

        // Soft ramp on engage/disengage so band transitions don't click.
        private const val RAMP_SECONDS: Float = 3f

        private const val TWO_PI: Double = 2.0 * Math.PI

        private fun approach(current: Float, target: Float, step: Float): Float {
            if (current == target) return target
            return if (current < target) {
                (current + step).coerceAtMost(target)
            } else {
                (current - step).coerceAtLeast(target)
            }
        }
    }
}
