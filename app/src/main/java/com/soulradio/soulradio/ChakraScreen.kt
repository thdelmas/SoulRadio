package com.soulradio.soulradio

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Chakra — the body-centre map. A fourth way to read the field of sound:
 * not by tradition (Solfeggio, the dial), not by Hz (the radio), not by
 * what the listener reaches *for* ([BodyScreen]'s lever map), but by where
 * the modern Solfeggio→chakra pairing locates the tone in the body.
 *
 * Seven canonical centres, root → crown:
 * - **Muladhara** (root) ↔ 396.
 * - **Svadhisthana** (sacral) ↔ 417.
 * - **Manipura** (solar plexus) ↔ 528.
 * - **Anahata** (heart) ↔ 639.
 * - **Vishuddha** (throat) ↔ 741.
 * - **Ajna** (third eye) ↔ 852.
 * - **Sahasrara** (crown) ↔ 963.
 *
 * Plus a closing section on the four bands that sit *outside* the seven —
 * 174 / 285 (below the root in the Solfeggio set, no chakra in the older
 * yogic anatomy), 432 (a tuning standard, not a Solfeggio tone), and
 * 7.83 (sub-audible cadence, not a tone the body locates).
 *
 * Reached from the bottom of [BodyScreen] as a "see also" link rather
 * than a sixth strip pill — the [ModeStrip] is already at its width
 * budget, and the dial's "stays at eleven so the room can recede" rule
 * applies equally to the navigation strip. Chakra is peer-class with
 * [BodyScreen] (same screen-class, no audio of its own); the entry path
 * is one click in.
 *
 * Per [MANIFESTO.md](../../../../../../MANIFESTO.md): no medical claims.
 * Same status as the **Folk name** field in [FREQUENCIES.md][freqs]: the
 * tradition's own anatomy, *reported* by the radio, never *endorsed*.
 * The body decides whether it agrees.
 *
 * [freqs]: ../../../../../../FREQUENCIES.md
 */
@Composable
fun ChakraScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Pause the dial's player while the chakra map is open — same
    // lifecycle as [BodyScreen] and [RadioModeScreen]; a cartography page
    // is for reading, not for listening.
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

            ChakraSection(
                sanskrit = "Muladhara",
                english = "root",
                location = "base of the spine · 396 Hz",
                body = "The first chakra in the canonical seven. *Mūlādhāra* — *mūla* (root) + *ādhāra* (support) — names the body's ground: the pelvic floor, the soles of the feet, the bones the body returns its weight to. The Solfeggio→chakra pairing places 396 here — the dial's *morning gate* — for the same gesture: the place a practice opens from, the foot before the next step. The same threshold, named twice.",
                whereInTheRadio = "dial · 396 (the morning gate; auto loop, 06:00–09:00).",
            )

            ChakraSection(
                sanskrit = "Svadhisthana",
                english = "sacral",
                location = "below the navel · 417 Hz",
                body = "The second chakra. *Svādhiṣṭhāna* — \"one's own seat\" — names the lower abdomen, the hips, the water of the body. The tradition pairs it with movement, fluidity, and transition; the Solfeggio→chakra mapping aligns it with 417 — *the dissolver* — for the same reason: the place the day's residue softens rather than is forced out. The dial's *softening rather than forcing* is the Solfeggio voice for this chakra's same gesture.",
                whereInTheRadio = "dial · 417 (the dissolver; auto loop, 18:00–20:00).",
            )

            ChakraSection(
                sanskrit = "Manipura",
                english = "solar plexus",
                location = "upper abdomen · 528 Hz",
                body = "The third chakra. *Maṇipūra* — \"city of jewels\" — names the upper abdomen, the diaphragm, the seat of warmth. The Solfeggio→chakra pairing places 528 here — *the centre* — and the dial's name uses the same word; the tradition and the dial converge on a single noun. (Some modern sources misfile 528 at the heart instead, conflating *love frequency* with the heart chakra. The older Solfeggio→chakra mapping keeps 528 at the solar plexus and reserves the heart for 639.)",
                whereInTheRadio = "dial · 528 (the centre; auto loop, 12:00–16:00 — the loop's longest single band).",
            )

            ChakraSection(
                sanskrit = "Anahata",
                english = "heart",
                location = "chest centre · 639 Hz",
                body = "The fourth chakra and the centre of the seven. *Anāhata* — \"unstruck\" — names the heart in yogic acoustics: the sound that exists without a striker, the body's own resonance heard in stillness. The tradition pairs it with relationship, agreement, and the meeting of independent voices — the architecture-of-agreement intention the dial's 639 band already carries (Renaissance polyphony, Bach's three-by-three Brandenburg, the *santur* in conversation with an ensemble).",
                whereInTheRadio = "dial · 639 (the table; auto loop, 16:00–18:00).",
            )

            ChakraSection(
                sanskrit = "Vishuddha",
                english = "throat",
                location = "throat · 741 Hz",
                body = "The fifth chakra. *Viśuddha* — \"purest\" — names the throat, the larynx, the place sound becomes speech. The tradition pairs it with articulation and the rinse of the head; the Solfeggio→chakra mapping aligns it with 741 — *the clearing* — for the same reason. The dial's keyboard-led repertoire on this band (Bach's Goldberg Aria, a Couperin *pièce*, a Scarlatti sonata, a Chaminade flute concertino) is articulate single-line music, audibly thinking — the texture the throat chakra is said to ask for.",
                whereInTheRadio = "dial · 741 (the clearing; auto loop, 09:00–12:00).",
            )

            ChakraSection(
                sanskrit = "Ajna",
                english = "third eye",
                location = "between the brows · 852 Hz",
                body = "The sixth chakra. *Ājñā* — \"command\" or \"perception\" — names the point between the brows, the upper face, the place perception is felt to *see from*. The tradition pairs it with altitude, stepping back, the long view; the Solfeggio→chakra mapping aligns it with 852 — *the high window* — for the same reason. The dial keeps 852 as a tap-only band: vertical air is something a body has to ask for, and the auto loop has no business surprising someone with it (FREQUENCIES.md § Three modes).",
                whereInTheRadio = "dial · 852 (the high window; tap-only, never autoplayed).",
            )

            ChakraSection(
                sanskrit = "Sahasrara",
                english = "crown",
                location = "top of the head · 963 Hz",
                body = "The seventh and highest chakra. *Sahasrāra* — \"thousand-petalled\" — names the top of the head, the upper limit of the body's vertical, the meeting place with what is above. The tradition pairs it with arrival; the dial's 963 is named *the crown* for the same word. Three traditions arrive at this register from different directions on the band — Bach's *Sanctus*, the Byzantine *cherubic hymn*, the guqin's *Liu Shui* — and each earns its altitude only because it has spent time on the ground first. The dial keeps 963 tap-only, like 852: arrival is something a listener has to reach for.",
                whereInTheRadio = "dial · 963 (the crown; tap-only, never autoplayed).",
            )

            OutsideTheSeven()

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun Preamble() {
    Text(
        text = "the body-centre map",
        color = Gold,
        fontSize = 22.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(10.dp))
    Text(
        text = "A fourth way to read the field. The dial names tones by tradition (Solfeggio); the radio names them by Hz; the body names them by what a listener reaches for; the chakra map names them by where the Solfeggio→chakra pairing locates the tone in the body. The tradition's own anatomy, not the radio's. Reported, not endorsed; the body decides whether it agrees.",
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun ChakraSection(
    sanskrit: String,
    english: String,
    location: String,
    body: String,
    whereInTheRadio: String,
) {
    Text(
        text = "$sanskrit — $english".uppercase(),
        color = Gold,
        fontSize = 12.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = location,
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
private fun OutsideTheSeven() {
    Text(
        text = "OUTSIDE THE SEVEN",
        color = Gold,
        fontSize = 12.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "174 · 285 · 432 · 7.83",
        color = GoldDim,
        fontSize = 13.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(12.dp))
    HairlineDivider()
    Spacer(Modifier.height(14.dp))
    Text(
        text = "Four bands on the radio sit outside the canonical chakra map.\n\n" +
            "**174 and 285** are the two lowest tones in the modern Solfeggio set, both *below* the root in the seven-chakra schema. Some modern Solfeggio teachers pair them with sub-personal centres — an \"earth-star\" below the body for 174, the etheric / soft-tissue field for 285 — but these are recent additions outside the older yogic anatomy. The radio reads them as ground notes, not body centres.\n\n" +
            "**432 (Verdi's A)** is a *tuning standard* — the pitch the note A is set to — not a Solfeggio tone, so the Solfeggio→chakra pairing has no entry for it. The acoustic-era voice this companion carries is sat *with*, not located at a body centre.\n\n" +
            "**7.83 (Schumann)** is sub-audible. The chakra schema pairs *tones* with body centres; 7.83 is a cadence, not a tone the body locates. The radio frames it as the room rather than a station, and the chakra map has nothing to mount on it.",
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Light,
    )
    Spacer(Modifier.height(16.dp))
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
