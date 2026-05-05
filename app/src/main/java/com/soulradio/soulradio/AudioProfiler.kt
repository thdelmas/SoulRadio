package com.soulradio.soulradio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Pure-JVM audio analyzer that reads a recording's biohacking profile from
 * decoded PCM samples. The framework — three layers, spectral / temporal /
 * spatial — is documented at docs/tunables.md § Reading a recording's
 * profile.
 *
 * The output is descriptive, never prescriptive. This file names what is in
 * the recording (dominant Hz, tilt, BPM, sub-audible energy); it does not
 * claim what the recording will do to a listener — see MANIFESTO.md §5.
 *
 * Multi-band matching: a single recording can match multiple Solfeggio
 * bands (octave coincidences, overtone alignments). It can also match
 * none — ancestral / tribal recordings without a sustained tonic but with
 * a 4 Hz drum pulse and sub-60 percussion are the canonical case. An empty
 * matches list is not a rejection; the [ProfileSignals] still describe the
 * file, and the listener files it manually.
 */
object AudioProfiler {

    /**
     * The dial bands the auto-profiler matches against, with their numeric
     * Hz values. Excludes 7.83 (sub-audible — never matches a foreground
     * pitch; recordings with high sub-60 energy surface that via [ProfileSignals.sub60HzEnergyFraction]
     * instead, and the listener can file them on the night band manually).
     */
    private val BAND_HZ = linkedMapOf(
        "174" to 174.0,
        "285" to 285.0,
        "396" to 396.0,
        "417" to 417.0,
        "432" to 432.0,
        "528" to 528.0,
        "639" to 639.0,
        "741" to 741.0,
        "852" to 852.0,
        "963" to 963.0,
    )

    /** Match threshold, in cents (a quarter-tone). */
    private const val MATCH_THRESHOLD_CENTS = 50f

    /** FFT size; bin width = sampleRate / FFT_SIZE. 8192 gives ~5.4 Hz/bin at 44.1 kHz, fine to separate adjacent Solfeggio bands. */
    private const val FFT_SIZE = 8192
    private const val HOP_SIZE = 4096

    /**
     * Profile a chunk of PCM. [samples] is interleaved if [channels] > 1;
     * each sample is in [-1, 1]. Returns a [BandAssignment] — never null.
     * Inputs too short for one FFT frame yield empty signals + empty
     * matches, which the caller can recognise as "could not analyse."
     */
    fun profile(samples: FloatArray, channels: Int, sampleRate: Int): BandAssignment {
        val mono = mix(samples, channels)
        val avgSpectrum = welchSpectrum(mono)
        val bpm = detectBpm(mono, sampleRate)
        val tilt = spectralTilt(avgSpectrum, sampleRate)
        val sub60 = sub60HzFraction(avgSpectrum, sampleRate)
        val dom = dominantHz(avgSpectrum, sampleRate)
        val signals = ProfileSignals(
            bpm = bpm,
            spectralTiltDbPerOctave = tilt,
            sub60HzEnergyFraction = sub60,
            dominantHz = dom,
        )
        val matches = if (dom != null) matchBands(dom) else emptyList()
        return BandAssignment(matches, signals)
    }

    private fun mix(samples: FloatArray, channels: Int): FloatArray {
        if (channels == 1) return samples
        val mono = FloatArray(samples.size / channels)
        for (i in mono.indices) {
            var sum = 0f
            for (c in 0 until channels) sum += samples[i * channels + c]
            mono[i] = sum / channels
        }
        return mono
    }

    /**
     * Welch's method: average windowed-FFT magnitudes across overlapping
     * frames. Stable spectral estimate; smooths transients so the dominant
     * peak reflects the recording's sustained pitch rather than a single
     * moment.
     */
    private fun welchSpectrum(mono: FloatArray): FloatArray {
        val accum = FloatArray(FFT_SIZE / 2)
        if (mono.size < FFT_SIZE) return accum
        val window = hannWindow(FFT_SIZE)
        val real = FloatArray(FFT_SIZE)
        val imag = FloatArray(FFT_SIZE)
        var frames = 0
        var pos = 0
        while (pos + FFT_SIZE <= mono.size) {
            for (i in 0 until FFT_SIZE) {
                real[i] = mono[pos + i] * window[i]
                imag[i] = 0f
            }
            Fft.forward(real, imag)
            val mag = Fft.magnitudes(real, imag)
            for (i in accum.indices) accum[i] += mag[i]
            frames++
            pos += HOP_SIZE
        }
        if (frames > 0) {
            val divisor = frames.toFloat()
            for (i in accum.indices) accum[i] /= divisor
        }
        return accum
    }

    private fun hannWindow(n: Int): FloatArray =
        FloatArray(n) { i -> (0.5 * (1.0 - cos(2.0 * PI * i / (n - 1)))).toFloat() }

    private fun binToHz(bin: Double, sampleRate: Int): Double = bin * sampleRate / FFT_SIZE
    private fun hzToBin(hz: Double, sampleRate: Int): Int = (hz * FFT_SIZE / sampleRate).toInt()

    /**
     * Strongest peak above 50 Hz, with parabolic interpolation between
     * adjacent bins for sub-bin Hz accuracy. Returns null if the spectrum
     * is empty / silent, or if the peak above 50 Hz is just spectral
     * leakage from a sub-audible signal — guarded by requiring the
     * above-50 Hz peak to be at least half the *global* maximum, so a
     * 42 Hz sine's leaked sidelobes don't surface as a "57 Hz dominant."
     */
    private fun dominantHz(spectrum: FloatArray, sampleRate: Int): Float? {
        val minBin = hzToBin(50.0, sampleRate).coerceAtLeast(1)
        if (minBin >= spectrum.size) return null
        var globalMax = 0f
        for (m in spectrum) if (m > globalMax) globalMax = m
        if (globalMax == 0f) return null
        var peakBin = -1
        var peakMag = 0f
        for (i in minBin until spectrum.size) {
            if (spectrum[i] > peakMag) { peakMag = spectrum[i]; peakBin = i }
        }
        if (peakBin < 0 || peakMag == 0f) return null
        // Leakage gate: a real foreground peak dominates the spectrum;
        // sidelobes of a sub-audible peak do not.
        if (peakMag < 0.5f * globalMax) return null
        val refined = if (peakBin in 1 until spectrum.size - 1) {
            val a = spectrum[peakBin - 1]
            val b = spectrum[peakBin]
            val c = spectrum[peakBin + 1]
            val denom = a - 2 * b + c
            val offset = if (abs(denom) > 1e-6f) 0.5f * (a - c) / denom else 0f
            peakBin + offset
        } else peakBin.toFloat()
        return binToHz(refined.toDouble(), sampleRate).toFloat()
    }

    /** Fraction of total spectral *energy* (magnitude²) below 60 Hz. */
    private fun sub60HzFraction(spectrum: FloatArray, sampleRate: Int): Float {
        val cutoffBin = hzToBin(60.0, sampleRate).coerceAtMost(spectrum.size - 1)
        var below = 0.0
        var total = 0.0
        for (i in spectrum.indices) {
            val e = spectrum[i].toDouble() * spectrum[i].toDouble()
            total += e
            if (i <= cutoffBin) below += e
        }
        return if (total > 0) (below / total).toFloat() else 0f
    }

    /**
     * Linear least-squares fit of magnitude (dB) vs log2(Hz). Slope ≈ 0
     * for white, ≈ −3 for pink, ≈ −6 for brown. Skips DC + the highest 10%
     * (anti-alias roll-off would bias the slope).
     */
    private fun spectralTilt(spectrum: FloatArray, sampleRate: Int): Float {
        val startBin = hzToBin(40.0, sampleRate).coerceAtLeast(1)
        val endBin = hzToBin(min(sampleRate / 2.5, 16_000.0), sampleRate)
            .coerceAtMost(spectrum.size - 1)
        if (endBin <= startBin + 4) return 0f
        var sumX = 0.0; var sumY = 0.0; var sumXX = 0.0; var sumXY = 0.0; var n = 0
        for (i in startBin..endBin) {
            val mag = spectrum[i].toDouble()
            if (mag <= 0) continue
            val x = log2(binToHz(i.toDouble(), sampleRate))
            val y = 20.0 * log10(mag)
            sumX += x; sumY += y; sumXX += x * x; sumXY += x * y; n++
        }
        if (n < 4) return 0f
        val denom = n * sumXX - sumX * sumX
        if (abs(denom) < 1e-9) return 0f
        return ((n * sumXY - sumX * sumY) / denom).toFloat()
    }

    /**
     * Tempo via energy-difference onset envelope + autocorrelation. Searches
     * the 60–200 BPM range. Returns null if the input is too short or the
     * autocorrelation has no clear peak (silence, drone-only).
     */
    private fun detectBpm(mono: FloatArray, sampleRate: Int): Float? {
        val frameSize = sampleRate / 100 // 10 ms frames
        if (frameSize < 16) return null
        val frames = mono.size / frameSize
        if (frames < 200) return null
        val envelope = FloatArray(frames)
        for (f in 0 until frames) {
            var e = 0f
            for (i in 0 until frameSize) {
                val s = mono[f * frameSize + i]; e += s * s
            }
            envelope[f] = sqrt(e / frameSize)
        }
        val onset = FloatArray(frames)
        for (i in 1 until frames) {
            val d = envelope[i] - envelope[i - 1]
            onset[i] = if (d > 0) d else 0f
        }
        // Cover the shamanic-drum range (240–270 BPM, the Theta-bridge
        // signature in tunables.md) up to slow contemplative tempi.
        val minLag = 100 * 60 / 300 // 300 BPM → 20 frames
        val maxLag = 100 * 60 / 60  //  60 BPM → 100 frames
        if (maxLag >= onset.size / 2) return null
        var bestLag = -1
        var bestCorr = 0.0
        for (lag in minLag..maxLag) {
            var c = 0.0
            for (i in lag until onset.size) c += onset[i].toDouble() * onset[i - lag]
            if (c > bestCorr) { bestCorr = c; bestLag = lag }
        }
        if (bestLag < 0 || bestCorr <= 0) return null
        return 60f * 100f / bestLag
    }

    /**
     * Match a dominant pitch to the closest octave of every band, return
     * those within [MATCH_THRESHOLD_CENTS]. Confidence = (threshold − cents)
     * / threshold, so an exact match scores 1.0 and a quarter-tone offset
     * scores 0. Sorted by confidence descending.
     */
    private fun matchBands(dominantHz: Float): List<BandMatch> {
        val out = mutableListOf<BandMatch>()
        for ((key, baseHz) in BAND_HZ) {
            val ratio = dominantHz / baseHz
            if (ratio <= 0) continue
            val octaves = round(log2(ratio))
            // Octave-fold within ±2 octaves only. A 60 Hz drum carrier
            // four octaves below 963 Hz is not a "match" in any musical
            // sense — it is octave folding chasing arithmetic ghosts.
            if (abs(octaves) > 2) continue
            val target = baseHz * 2.0.pow(octaves)
            val cents = abs(1200.0 * log2(dominantHz / target)).toFloat()
            if (cents > MATCH_THRESHOLD_CENTS) continue
            val confidence = (1f - cents / MATCH_THRESHOLD_CENTS).coerceIn(0f, 1f)
            val reason = if (octaves == 0.0) {
                "dominant pitch %.1f Hz".format(dominantHz)
            } else {
                "dominant pitch %.1f Hz, octave of %.1f Hz".format(dominantHz, target)
            }
            out += BandMatch(key, confidence, reason)
        }
        out.sortByDescending { it.confidence }
        return out
    }
}
