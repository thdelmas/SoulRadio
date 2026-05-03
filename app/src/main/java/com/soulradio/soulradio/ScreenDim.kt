package com.soulradio.soulradio

/**
 * The screen-content dim factor for the band currently playing in the
 * auto loop. The radio recedes; so should the screen.
 *
 * Applied as a single Modifier.alpha on the screen's content layer — the
 * Bg surface underneath stays solid black, so only the gold-on-black glow
 * dims. At deep night the content drops to 0.6; at midday it's full
 * brightness. Between, the curve follows the diurnal arc mapped in
 * FREQUENCIES.md § Quick map.
 *
 * Source of truth is the same band [Frequencies.forNow] returns, so the
 * dim updates at the same band boundaries the auto loop does.
 *
 * See issue #24.
 */
internal fun screenDimFor(key: String): Float = when (key) {
    "528"        -> 1.00f  // 528 — straddles solar noon, full brightness
    "741", "639" -> 0.95f  // clearing / table — mid-morning, afternoon
    "396", "417" -> 0.85f  // dawn first-light / golden-hour dusk
    "285"        -> 0.75f  // slow turn — full dark settling
    "174"        -> 0.70f  // foundation — late evening
    "7.83"       -> 0.60f  // deep night — Sant'Antimo under stone reverb
    else         -> 1.00f
}
