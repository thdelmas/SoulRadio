package com.soulradio.soulradio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Cooley-Tukey radix-2 FFT, in-place. Pure JVM; no Android imports, so it
 * runs in unit tests without the device. Used by [AudioProfiler] to read the
 * spectral half of a recording's profile (see docs/tunables.md § Reading a
 * recording's profile).
 *
 * Hand-rolled rather than pulling JTransforms — keeps the dep list at the
 * size CLAUDE.md asks for, and the transform is small enough to read.
 */
internal object Fft {

    /** Forward FFT in place. [real] and [imag] must be the same power-of-2 length. */
    fun forward(real: FloatArray, imag: FloatArray) {
        val n = real.size
        require(imag.size == n) { "real and imag must be the same length" }
        require(n > 0 && n and (n - 1) == 0) { "length must be a power of two, got $n" }

        // Bit-reversal permutation.
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j or bit
            if (i < j) {
                val tr = real[i]; real[i] = real[j]; real[j] = tr
                val ti = imag[i]; imag[i] = imag[j]; imag[j] = ti
            }
        }

        // Butterflies.
        var len = 2
        while (len <= n) {
            val ang = -2.0 * PI / len
            val wReal0 = cos(ang).toFloat()
            val wImag0 = sin(ang).toFloat()
            var i = 0
            while (i < n) {
                var wr = 1f
                var wi = 0f
                for (k in 0 until len / 2) {
                    val xR = real[i + k + len / 2]
                    val xI = imag[i + k + len / 2]
                    val tR = wr * xR - wi * xI
                    val tI = wr * xI + wi * xR
                    val uR = real[i + k]
                    val uI = imag[i + k]
                    real[i + k] = uR + tR
                    imag[i + k] = uI + tI
                    real[i + k + len / 2] = uR - tR
                    imag[i + k + len / 2] = uI - tI
                    val newWr = wr * wReal0 - wi * wImag0
                    wi = wr * wImag0 + wi * wReal0
                    wr = newWr
                }
                i += len
            }
            len = len shl 1
        }
    }

    /** Magnitude spectrum after a forward FFT. Output length = real.size / 2 (Nyquist). */
    fun magnitudes(real: FloatArray, imag: FloatArray): FloatArray {
        val n = real.size / 2
        return FloatArray(n) { i -> sqrt(real[i] * real[i] + imag[i] * imag[i]) }
    }
}
