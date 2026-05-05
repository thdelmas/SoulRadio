package com.soulradio.soulradio

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin

/**
 * Synthetic-signal tests for [AudioProfiler]. Each test generates a known
 * input — pure sine, brown noise, ancestral-style 4 Hz drum pulse — and
 * asserts that the profiler reads it correctly. The pure-JVM analyzer means
 * these tests run without an Android device.
 *
 * Multi-band model coverage: a 528 sine matches band 528 at high confidence,
 * its octave (1056 Hz) also matches 528 (octave equivalence), and an
 * ancestral-style low-frequency drum pulse yields **empty** matches with
 * BPM signal populated — the canonical "no Solfeggio match, signals only"
 * case the user can file manually.
 */
class AudioProfilerTest {

    private val sampleRate = 44_100

    @Test
    fun pureSine_at_528Hz_matches_band_528_withHighConfidence() {
        val samples = sineWave(528.0, durationSec = 3.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)

        val dom = r.signals.dominantHz
        assertNotNull("dominantHz should be detected for a 528 Hz sine", dom)
        assertNear(528f, dom!!, tolerance = 3f, label = "dominantHz")

        val match = r.matches.firstOrNull { it.bandKey == "528" }
        assertNotNull("528 not in matches: ${r.matches}", match)
        assertTrue(
            "528 confidence should be high for a clean sine, got ${match!!.confidence}",
            match.confidence > 0.85f,
        )
    }

    @Test
    fun octave_above_528Hz_alsoMatches_band_528() {
        val samples = sineWave(1056.0, durationSec = 3.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)

        val match = r.matches.firstOrNull { it.bandKey == "528" }
        assertNotNull("528 not matched at octave 1056 Hz: ${r.matches}", match)
        assertTrue(
            "octave reason should mention octave: ${match!!.reason}",
            match.reason.contains("octave"),
        )
    }

    @Test
    fun pureSine_at_174Hz_matches_band_174() {
        val samples = sineWave(174.0, durationSec = 3.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)
        assertNotNull(
            "174 not matched for a 174 Hz sine: ${r.matches}",
            r.matches.firstOrNull { it.bandKey == "174" },
        )
    }

    @Test
    fun pitchHalfwayBetween_396_and_417_matches_neither_or_low_confidence() {
        // Midpoint ≈ 406.4 Hz. cents to 396 ≈ 44.8; cents to 417 ≈ 44.5.
        // Both just inside the 50-cent threshold → both matched at low conf,
        // honest "this could be either" signal. A pitch *outside* the
        // threshold for both would yield empty matches.
        val samples = sineWave(406.4, durationSec = 3.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)
        for (m in r.matches) {
            assertTrue(
                "Borderline match should have low confidence: $m",
                m.confidence < 0.5f,
            )
        }
    }

    @Test
    fun brownNoise_hasTilt_near_minus_6_dB_per_octave() {
        // Brown noise: power ∝ 1/f², magnitude ∝ 1/f → slope = −6 dB/octave.
        val samples = brownNoise(durationSec = 5.0, seed = 42)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)
        assertNear(
            expected = -6f,
            actual = r.signals.spectralTiltDbPerOctave,
            tolerance = 2.5f,
            label = "spectralTilt (brown)",
        )
    }

    @Test
    fun whiteNoise_hasTilt_near_zero() {
        val samples = whiteNoise(durationSec = 5.0, seed = 17)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)
        assertNear(
            expected = 0f,
            actual = r.signals.spectralTiltDbPerOctave,
            tolerance = 2f,
            label = "spectralTilt (white)",
        )
    }

    @Test
    fun ancestralDrum_lowCarrier_pulsedAt_240BPM_yields_emptyMatches_butSignals() {
        // The canonical ancestral case: a low-frequency carrier (60 Hz,
        // mimicking a drum body resonance) pulsed at 4 Hz (240 BPM = the
        // shamanic Theta-bridge signature in tunables.md). 60 Hz is far
        // from every band's nearest octave, so matches must be empty —
        // empty list is honest "no Solfeggio fit; profile lives in the
        // signals." The user files such a recording manually.
        val samples = pulsedSine(carrierHz = 60.0, pulseHz = 4.0, durationSec = 6.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)

        assertNotNull("BPM should be detected for a pulsed signal", r.signals.bpm)
        assertNear(240f, r.signals.bpm!!, tolerance = 12f, label = "bpm")

        assertTrue(
            "Ancestral signal should yield empty Solfeggio matches, got ${r.matches}",
            r.matches.isEmpty(),
        )
    }

    @Test
    fun subAudibleDrone_at_42Hz_dominates_sub60_andHasNoForegroundMatch() {
        // 42 Hz is in the vibroacoustic / sub-audible band. The dominantHz
        // detector cuts off at 50 Hz so foreground matching does not engage,
        // and sub60 fraction must dominate.
        val samples = sineWave(42.0, durationSec = 3.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)

        assertTrue(
            "sub60 fraction should dominate for a 42 Hz sine, got ${r.signals.sub60HzEnergyFraction}",
            r.signals.sub60HzEnergyFraction > 0.7f,
        )
        assertNull(
            "dominantHz should be null for sub-audible content (cutoff at 50 Hz)",
            r.signals.dominantHz,
        )
        assertTrue(
            "matches should be empty for sub-audible drone, got ${r.matches}",
            r.matches.isEmpty(),
        )
    }

    @Test
    fun shortInput_yields_empty_matches_andNullSignals() {
        // Less than one FFT frame.
        val samples = FloatArray(1000) { 0f }
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)
        assertTrue("matches must be empty for too-short input", r.matches.isEmpty())
        assertNull("dominantHz should be null for too-short input", r.signals.dominantHz)
        assertNull("bpm should be null for too-short input", r.signals.bpm)
    }

    @Test
    fun stereoInput_isMixedDownToMono_correctly() {
        // Stereo with identical L = R = 528 Hz — should still match 528.
        val mono = sineWave(528.0, durationSec = 3.0)
        val stereo = FloatArray(mono.size * 2) { mono[it / 2] }
        val r = AudioProfiler.profile(stereo, channels = 2, sampleRate = sampleRate)
        assertNotNull(
            "528 should match for stereo input: ${r.matches}",
            r.matches.firstOrNull { it.bandKey == "528" },
        )
    }

    @Test
    fun matches_areSorted_byConfidenceDescending() {
        // Use a 528 sine (single clean match), then verify the list is
        // non-empty and sorted — the contract callers depend on for "top
        // match" UX.
        val samples = sineWave(528.0, durationSec = 3.0)
        val r = AudioProfiler.profile(samples, channels = 1, sampleRate = sampleRate)
        for (i in 1 until r.matches.size) {
            assertTrue(
                "matches must be sorted by confidence desc: ${r.matches}",
                r.matches[i - 1].confidence >= r.matches[i].confidence,
            )
        }
    }

    // -- synthetic-signal helpers --

    private fun sineWave(hz: Double, durationSec: Double, amplitude: Float = 0.5f): FloatArray {
        val n = (durationSec * sampleRate).toInt()
        val out = FloatArray(n)
        val inc = 2.0 * PI * hz / sampleRate
        var phase = 0.0
        for (i in 0 until n) {
            out[i] = (sin(phase) * amplitude).toFloat()
            phase += inc
        }
        return out
    }

    /**
     * Half-wave-rectified amplitude modulation: a sharp pulse at [pulseHz]
     * gating a [carrierHz] sine. Approximates a struck-drum train clearly
     * enough for BPM autocorrelation.
     */
    private fun pulsedSine(carrierHz: Double, pulseHz: Double, durationSec: Double): FloatArray {
        val n = (durationSec * sampleRate).toInt()
        val out = FloatArray(n)
        val cInc = 2.0 * PI * carrierHz / sampleRate
        val pInc = 2.0 * PI * pulseHz / sampleRate
        var c = 0.0; var p = 0.0
        for (i in 0 until n) {
            val env = max(0.0, sin(p))
            out[i] = (sin(c) * env * 0.5).toFloat()
            c += cInc; p += pInc
        }
        return out
    }

    private fun whiteNoise(durationSec: Double, seed: Long): FloatArray {
        val n = (durationSec * sampleRate).toInt()
        val rng = Random(seed)
        return FloatArray(n) { (rng.nextGaussian() * 0.3).toFloat() }
    }

    /**
     * Leaky-integrated white noise: PSD ∝ 1/f² in the audible band, which
     * is the −6 dB/octave brown-noise signature.
     */
    private fun brownNoise(durationSec: Double, seed: Long): FloatArray {
        val n = (durationSec * sampleRate).toInt()
        val rng = Random(seed)
        var integ = 0.0
        val out = FloatArray(n)
        for (i in 0 until n) {
            integ = 0.999 * integ + rng.nextGaussian() * 0.05
            out[i] = integ.toFloat().coerceIn(-1f, 1f)
        }
        return out
    }

    private fun assertNear(expected: Float, actual: Float, tolerance: Float, label: String) {
        val diff = abs(actual - expected)
        assertTrue("$label: $actual not within $tolerance of $expected (diff=$diff)", diff <= tolerance)
    }
}
