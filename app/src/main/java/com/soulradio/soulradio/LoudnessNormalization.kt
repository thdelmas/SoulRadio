package com.soulradio.soulradio

import android.content.Context
import org.json.JSONObject
import kotlin.math.min
import kotlin.math.pow

// EBU R128 normalization for the curated catalogue. Per-asset integrated
// LUFS measurements live in assets/loudness.json (offline pass via
// scripts/measure-loudness.sh); factorFor() returns the linear multiplier
// that, applied to TARGET_VOLUME, pulls each track toward TARGET_LUFS so
// band rotations don't step the listener through ±15 dB jumps. Manifesto
// §6 — volumes that do not fatigue.
//
// User-library URIs (content://...) and unmeasured asset URIs return 1.0,
// so the normalization domain is exactly the curated catalogue.
object LoudnessNormalization {
    private const val TARGET_LUFS = -16.0
    // Cap boost so the noise floor on century-old recordings stays bearable.
    // The +12 dB ceiling means a -33 LUFS file lands ~5 LUFS below target —
    // still quieter than its neighbours, but not a 17 dB step.
    private const val MAX_BOOST_DB = 12.0
    private const val ASSET_PREFIX = "asset:///audio/"

    @Volatile private var cached: Map<String, Double>? = null

    fun factorFor(context: Context, uri: String): Float {
        if (!uri.startsWith(ASSET_PREFIX)) return 1f
        val rel = uri.removePrefix(ASSET_PREFIX)
        val lufs = lufsMap(context)[rel] ?: return 1f
        val gainDb = min(TARGET_LUFS - lufs, MAX_BOOST_DB)
        return 10.0.pow(gainDb / 20.0).toFloat()
    }

    private fun lufsMap(context: Context): Map<String, Double> {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }
            val text = context.assets.open("loudness.json").bufferedReader().use { it.readText() }
            val obj = JSONObject(text)
            val out = HashMap<String, Double>(obj.length())
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                out[k] = obj.getDouble(k)
            }
            cached = out
            return out
        }
    }
}
