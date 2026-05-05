package com.soulradio.soulradio

/**
 * The descriptive signals [AudioProfiler] reads from a recording. Each one
 * is a property of the file, never a claim about what the file will do to
 * a listener — see docs/tunables.md § Reading a recording's profile and
 * MANIFESTO.md §5.
 *
 * - [bpm]: detected tempo, null when no clear pulse stands out. A pulse
 *   near 240 BPM (= 4 Hz) is the shamanic-drumming Theta-bridge signature.
 * - [spectralTiltDbPerOctave]: linear-fit slope of magnitude (dB) vs
 *   log2(Hz). 0 ≈ white noise, ≈ −3 ≈ pink, ≈ −6 ≈ brown.
 * - [sub60HzEnergyFraction]: fraction of total spectral energy below 60 Hz
 *   — vibroacoustic / sub-audible drone territory (Tibetan overtone, low
 *   percussion).
 * - [dominantHz]: strongest spectral peak above ~50 Hz, with sub-bin
 *   parabolic interpolation. The signal used to match the recording to
 *   Solfeggio bands.
 */
data class ProfileSignals(
    val bpm: Float?,
    val spectralTiltDbPerOctave: Float,
    val sub60HzEnergyFraction: Float,
    val dominantHz: Float?,
)

/**
 * One band the recording's signals matched, with a [confidence] in [0, 1]
 * and a short [reason] string for the UI to explain *why* (e.g., "dominant
 * pitch 527.4 Hz", "octave of 264 Hz tonic").
 */
data class BandMatch(
    val bandKey: String,
    val confidence: Float,
    val reason: String,
)

/**
 * The auto-profile result for a recording.
 *
 * [matches] is the ranked list of bands the recording's signals fit, by
 * descending confidence. A recording can match more than one band: a
 * Tibetan overtone chant with a clear tonic *and* a sub-60 Hz drone may
 * match a Solfeggio band on the tonic; an A=432 acoustic-era opera with a
 * pitch-class peak on A may match the 432 companion.
 *
 * [matches] can also be empty. An empty list does **not** mean reject —
 * it means the file lives on its [signals] alone. Ancestral / tribal
 * recordings are the canonical case: a shamanic drum has a 4 Hz pulse and
 * sub-60 percussion but no Solfeggio fundamental, so none of the 11 dial
 * bands matches. The Library UI surfaces the signals so the listener can
 * file the recording deliberately on whatever band fits their use of it.
 *
 * The curated catalogue still files each recording on one band — that is
 * the curator's editorial placement, not a claim that profile is
 * single-valued. User files (where the listener is the curator) can be
 * filed on multiple bands.
 */
data class BandAssignment(
    val matches: List<BandMatch>,
    val signals: ProfileSignals,
)
