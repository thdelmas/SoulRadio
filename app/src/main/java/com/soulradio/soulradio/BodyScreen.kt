package com.soulradio.soulradio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Body — the lever map. A third way to read the field of sound: not by
 * tradition (Solfeggio, the dial) and not by Hz (the radio), but by what
 * the listener reaches *for* — the register of nervous-system,
 * neurochemical, or compositional work the sound is being used for.
 *
 * Four registers:
 * - **Neurological** — brainwave entrainment (delta sleep ↔ gamma).
 * - **Autonomic** — vagus, HRV, parasympathetic down-regulation.
 * - **Neurochemical** — oxytocin / dopamine; "musical chills" and the
 *   slow-tempo / predictable-melody literature.
 * - **Music structure** — tempo, harmonic surprise, compositional
 *   density across the field.
 *
 * Each section names the dial / radio entries that live in the register
 * so the listener can move from "what am I reaching for" → "which
 * tradition or recording lives here." The body bundles no audio; it is
 * a map, not a sound.
 *
 * Per [MANIFESTO.md](../../../../../../MANIFESTO.md): no medical claims.
 * The body describes practice — what listeners and communities have
 * used these registers for, what the literature reports — not effect on
 * the reader's body. The voice mirrors [RadioModeScreen]'s descriptive
 * register, not a wellness-product prescriptive register.
 */
@Composable
fun BodyScreen(onOpenChakra: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Pause the dial's player while body is open so reading the map is
    // quiet. Resume on close — same lifecycle as [RadioModeScreen].
    DisposableEffect(Unit) {
        PlaybackService.pauseForRadio(context)
        onDispose {
            PlaybackService.resumeFromRadio(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        HairlineDivider()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 20.dp),
        ) {
            Preamble()
            Spacer(Modifier.height(28.dp))

            LeverSection(
                lever = "Neurological",
                subtitle = "brainwave entrainment",
                body = "Brainwave-entrainment audio uses sound rhythms to coax the brain toward target oscillations — delta for deep sleep, theta for trance and meditation, alpha for relaxed wakefulness, beta for problem-solving alertness, gamma for cross-cortical integration. The dominant delivery mechanisms — binaural beats, isochronic tones, monaural pulses — are speaker-or-headphone audio with no pre-electronic music tradition behind them; the radio documents them as labelled exhibits, not as curated music.\n\nThe one register with a real ancestral lineage is theta. Shamanic drumming traditions (Siberian, Native American, Mongolian) commonly hold a steady 240–270 BPM cadence — a 4 – 4.5 Hz pulse rate, squarely in the theta band. The dial's 7.83 night band sits at the top edge of theta; the radio frames it as a geophysical resonance, not a vibroacoustic claim.",
                whereInTheRadio = "dial · 7.83 (Schumann · night).  radio · Delta / Theta / Alpha / Beta / Gamma rows; Binaural / Isochronic / Monaural delivery; Schumann harmonics.",
            )

            LeverSection(
                lever = "Autonomic",
                subtitle = "vagus, HRV, parasympathetic",
                body = "The vagus-nerve / parasympathetic register. Practice covers a broad palette: low-frequency drones (chest and throat resonance) targeting auricular vagus stimulation; slow-tempo piano and string repertoire studied in the heart-rate-variability literature; vibroacoustic-therapy chairs and mats that bone-conduct low-frequency vibration into the body.\n\nDebussy's Clair de Lune is the most-cited single piece in the HRV / autonomic-regulation literature — Diez et al. and successors report a consistent rise in the high-frequency HRV component during listening. Throat-singing and overtone chant (Tibetan, Mongolian) reach the vagal-tone drone range as a side effect of musical work, which is the radio's distinction between admission of the singing and refusal of the bare drone.",
                whereInTheRadio = "dial · 432 (Verdi · day; slow acoustic-era recordings).  radio · Vagal-tone drone range 20–60 Hz; 128 Hz vibroacoustic; OM-tone 136.1; Cousto Earth day 194.18.",
            )

            LeverSection(
                lever = "Neurochemical",
                subtitle = "oxytocin, dopamine, frisson",
                body = "The reward / social-bonding register. Harmonic surprise — an unexpected chord, a deferred resolution — is associated with \"musical chills\" or frisson, studied as a brief dopamine response in the striatum. Slow-tempo piano (Chopin, Debussy) and choral repertoire correlate in the salivary-oxytocin literature with measurable rise. Group singing, chanting, and synchronised drumming amplify the effect via the social-bonding literature.\n\nMost of the dial's curated acoustic-era recordings live in this register: Bach cantatas, Gregorian chant, Renaissance polyphony, Romantic piano. The radio's reference-pitch entries — A=415 Baroque, A=421.6 Mozart-era, A=435 19th-c. French — are pointers to the same repertoire performed in period pitch.",
                whereInTheRadio = "dial · 396 / 528 / 639 (Solfeggio bands curated for chant and polyphony); 432 (Verdi pitch).  radio · A=415 / A=392 / A=421.6 / A=435 reference pitches.",
            )

            LeverSection(
                lever = "Music structure",
                subtitle = "tempo, density, predictability",
                body = "The compositional layer. Slow tempo (under ~60 BPM) and predictable melodic structure correlate in the cortisol literature with reduced salivary cortisol; complex spatial-temporal structures (the \"Mozart effect\" studies) are linked to short-term gains in math and engineering tasks. Steady rhythmic pulse — a drum at 4 Hz, the heartbeat's dominant — entrains motor and autonomic systems alike.\n\nThe dial's 24-hour DJ loop leans on this lever directly: slow predictable repertoire fills the night bands; mornings open into denser compositional structure as the listener wakes. The lever cuts across the field rather than living at any single Hz.",
                whereInTheRadio = "dial · the entire DJ loop (night = slow / predictable; day = denser / more complex).  radio · noise colors (white / pink / brown) as masking-by-spectrum.",
            )

            SeeAlsoChakra(onOpen = onOpenChakra)

            Spacer(Modifier.height(48.dp))
        }
    }
}

/**
 * Footer link to [ChakraScreen]. The chakra map is a peer-class cartography
 * surface — same screen-class as the body, named by tradition rather than
 * lever — but lives one click in from here rather than as a sixth strip
 * pill (the strip is already at its width budget). The link is plain text
 * with a hairline above so it reads as cross-reference, not call-to-action.
 */
@Composable
private fun SeeAlsoChakra(onOpen: () -> Unit) {
    HairlineDivider()
    Spacer(Modifier.height(18.dp))
    Text(
        text = "SEE ALSO",
        color = Gold,
        fontSize = 11.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(7.dp))
    Text(
        text = "the chakra map →",
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Light,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .padding(vertical = 6.dp),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "A fourth reading frame: the seven canonical chakras and where the Solfeggio→chakra pairing locates each tone in the body. Reported, not endorsed; the body decides whether it agrees.",
        color = MuteSoft,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun Preamble() {
    Text(
        text = "the lever map",
        color = Gold,
        fontSize = 22.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(10.dp))
    Text(
        text = "A third way to read the field. The dial names tones by tradition (Solfeggio); the radio names them by Hz; the body names them by what a listener reaches for — the register of nervous-system, neurochemical, or compositional work the sound is being used for. Descriptive of practice, not prescriptive of effect on the reader.",
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun LeverSection(
    lever: String,
    subtitle: String,
    body: String,
    whereInTheRadio: String,
) {
    Text(
        text = lever.uppercase(),
        color = Gold,
        fontSize = 12.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = subtitle,
        color = GoldDim,
        fontSize = 13.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(12.dp))
    HairlineDivider()
    Spacer(Modifier.height(14.dp))
    Text(
        text = body,
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Light,
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = "WHERE IN THE RADIO",
        color = Gold,
        fontSize = 11.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(7.dp))
    Text(
        text = whereInTheRadio,
        color = MuteSoft,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Light,
    )
    Spacer(Modifier.height(28.dp))
}

@Composable
private fun HairlineDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Hairline),
    )
}
