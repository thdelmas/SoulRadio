package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Tests the math of [SchumannUnderlay] — pass-through behaviour when
 * disabled, envelope shape when enabled, ramp progression. The Media3
 * AudioProcessor lifecycle (configure/queueInput/getOutput) is covered
 * by integration on a real device; here we exercise [modulate] directly.
 */
class SchumannUnderlayTest {

    private val sampleRate = 44_100

    @Test
    fun disabled_passesAudioThroughBitForBit() {
        val u = SchumannUnderlay().apply { configurePhaseIncrementForTest(sampleRate) }
        // rampValue defaults to 0; envelope = 1 + 0 * sin(phase) = 1.
        val frames = 1024
        val (input, output) = pcmBuffers(frames, channels = 2)
        fillSineWave(input, frames, channels = 2, freqHz = 440.0, amplitude = 16_000)

        u.modulate(input.asShortBuffer(), output.asShortBuffer(), channels = 2)

        // Ramp is still zero, so output must equal input.
        assertEquals(toList(input, frames * 2), toList(output, frames * 2))
    }

    @Test
    fun fullyRamped_envelopeFollows7_83HzSine() {
        val u = SchumannUnderlay().apply {
            configurePhaseIncrementForTest(sampleRate)
            // Skip the 3-second engagement ramp: park rampValue *and* the
            // target at full depth so approach() doesn't decay it during
            // the test window.
            setEnabled(true)
            setRampValueForTest(SchumannUnderlay.DEPTH)
            setPhaseForTest(0.0)
        }
        val frames = sampleRate // exactly one second
        val (input, output) = pcmBuffers(frames, channels = 1)
        // DC carrier of 10_000: any modulation reads cleanly off the output.
        fillConstant(input, frames, channels = 1, value = 10_000)

        u.modulate(input.asShortBuffer(), output.asShortBuffer(), channels = 1)

        val outShorts = output.asShortBuffer()
        // At phase 0, sin = 0, so first sample ≈ carrier.
        assertEquals(10_000, outShorts.get(0).toInt())

        // Quarter-period of 7.83 Hz lands at sample sampleRate/(7.83*4) ≈ 1408.
        // sin(π/2) = 1 → expected = carrier * (1 + DEPTH).
        val quarter = (sampleRate / (SchumannUnderlay.FREQUENCY_HZ * 4)).toInt()
        val peak = outShorts.get(quarter).toInt()
        val expected = (10_000f * (1f + SchumannUnderlay.DEPTH)).toInt()
        // Allow ±1% rounding error from float math + integer truncation.
        assertNear(expected, peak, tolerance = expected / 100 + 2)

        // Three-quarter-period: sin(3π/2) = -1 → carrier * (1 - DEPTH).
        val trough = outShorts.get(3 * quarter).toInt()
        val expectedTrough = (10_000f * (1f - SchumannUnderlay.DEPTH)).toInt()
        assertNear(expectedTrough, trough, tolerance = expectedTrough / 100 + 2)
    }

    @Test
    fun ramp_climbsFromZeroToDepthOverThreeSeconds() {
        val u = SchumannUnderlay().apply {
            configurePhaseIncrementForTest(sampleRate)
            setEnabled(true)
        }
        // Run a frame buffer worth of samples — short enough that ramp
        // hasn't finished — and check that rampValue moved partway.
        val frames = sampleRate // one second
        val (input, output) = pcmBuffers(frames, channels = 1)
        fillConstant(input, frames, channels = 1, value = 0)
        u.modulate(input.asShortBuffer(), output.asShortBuffer(), channels = 1)

        // After 1s of a 3s ramp the value should be ~DEPTH/3, well below DEPTH.
        val r = u.rampValueForTest()
        assertTrue("ramp made progress: $r", r > 0f)
        assertTrue("ramp not yet complete: $r", r < SchumannUnderlay.DEPTH)
        val expected = SchumannUnderlay.DEPTH / 3f
        assertTrue(
            "ramp roughly at one-third: $r vs expected $expected",
            kotlin.math.abs(r - expected) < SchumannUnderlay.DEPTH / 10f,
        )
    }

    @Test
    fun ramp_descendsBackToZeroWhenDisabled() {
        val u = SchumannUnderlay().apply {
            configurePhaseIncrementForTest(sampleRate)
            setRampValueForTest(SchumannUnderlay.DEPTH)
            setEnabled(false)
        }
        // Process a full ramp's worth (3 seconds) of silence.
        val frames = sampleRate * 3 + 100
        val (input, output) = pcmBuffers(frames, channels = 1)
        fillConstant(input, frames, channels = 1, value = 0)
        u.modulate(input.asShortBuffer(), output.asShortBuffer(), channels = 1)

        // Allow small float residual: ~132 k single-precision subtractions
        // accumulate rounding error; the audible effect at this scale is none.
        assertEquals(0f, u.rampValueForTest(), 1e-4f)
    }

    private fun pcmBuffers(frames: Int, channels: Int): Pair<ByteBuffer, ByteBuffer> {
        val bytes = frames * channels * 2
        val input = ByteBuffer.allocate(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val output = ByteBuffer.allocate(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return input to output
    }

    private fun fillConstant(buf: ByteBuffer, frames: Int, channels: Int, value: Int) {
        val s = buf.asShortBuffer()
        repeat(frames * channels) { s.put(value.toShort()) }
    }

    private fun fillSineWave(
        buf: ByteBuffer, frames: Int, channels: Int, freqHz: Double, amplitude: Int,
    ) {
        val s = buf.asShortBuffer()
        val inc = 2.0 * Math.PI * freqHz / sampleRate
        var phase = 0.0
        for (f in 0 until frames) {
            val v = (kotlin.math.sin(phase) * amplitude).toInt().toShort()
            for (c in 0 until channels) s.put(v)
            phase += inc
        }
    }

    private fun toList(buf: ByteBuffer, count: Int): List<Short> {
        val s = buf.asShortBuffer()
        return List(count) { s.get(it) }
    }

    private fun assertNear(expected: Int, actual: Int, tolerance: Int) {
        val diff = kotlin.math.abs(expected - actual)
        assertTrue("$actual not within $tolerance of $expected (diff=$diff)", diff <= tolerance)
    }
}
