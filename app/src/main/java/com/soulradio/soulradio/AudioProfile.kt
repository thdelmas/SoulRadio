package com.soulradio.soulradio

// spectralTiltDbPerOctave: 0 ≈ white, −3 ≈ pink, −6 ≈ brown.
data class ProfileSignals(
    val bpm: Float?,
    val spectralTiltDbPerOctave: Float,
    val sub60HzEnergyFraction: Float,
    val dominantHz: Float?,
)

data class BandMatch(
    val bandKey: String,
    val confidence: Float,
    val reason: String,
)

// Empty matches is canonical (e.g. ancestral drum has signals but no
// Solfeggio fundamental); the listener files such tracks manually.
data class BandAssignment(
    val matches: List<BandMatch>,
    val signals: ProfileSignals,
)
