package com.soulradio.soulradio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RadioModeScreen() {
    val scrollState = rememberScrollState()
    val sineDemo = remember { SineDemo() }
    PauseDialWhileOpen()
    DisposableEffect(sineDemo) {
        onDispose { sineDemo.release() }
    }

    // Coupling expand and play to one state keeps the audio model trivially
    // correct — at most one tone, ever.
    var activeHz by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(activeHz) {
        // AudioTrack start/stop may briefly block on thread join.
        withContext(Dispatchers.IO) {
            val hz = activeHz?.let { Catalogue.audibleHzFor(it) }
            if (hz != null) sineDemo.start(hz) else sineDemo.stop()
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
            Spacer(Modifier.height(24.dp))
            CatalogueGroup.entries.forEach { group ->
                Catalogue.byGroup[group]?.let { groupEntries ->
                    GroupSection(
                        group = group,
                        entries = groupEntries,
                        activeHz = activeHz,
                        onTap = { hz ->
                            activeHz = if (activeHz == hz) null else hz
                        },
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun Preamble() {
    Text(
        text = "the wider field",
        color = Gold,
        fontSize = 22.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(10.dp))
    Text(
        text = "Frequencies considered for the dial that did not earn a slot. The dial stays small so the room can recede; this is where the rest of the landscape is documented. Tap a row for history, how the frequency is used in sound-healing, biohacker, and musical practice, what studies show, concrete references, and when a listener might reach for it — audible Hz also play as a tone demo.",
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun GroupSection(
    group: CatalogueGroup,
    entries: List<CatalogueEntry>,
    activeHz: String?,
    onTap: (String) -> Unit,
) {
    Text(
        text = group.label,
        color = GoldDim,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(8.dp))
    HairlineDivider()
    entries.forEach { entry ->
        EntryRow(
            entry = entry,
            active = entry.hz == activeHz,
            onTap = { onTap(entry.hz) },
        )
    }
}

@Composable
private fun EntryRow(entry: CatalogueEntry, active: Boolean, onTap: () -> Unit) {
    val playable = remember(entry.hz) { Catalogue.audibleHzFor(entry.hz) != null }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Hz label fixed-width so titles align across rows. 72 dp covers
            // "111 … 999" without crowding the title; numeric Hz like "8" or
            // "1122" sit comfortably in the same column.
            Text(
                text = entry.hz,
                color = Gold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.width(72.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = entry.title,
                color = MuteSoft,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.weight(1f),
            )
            // The glyph column tells the listener two things at once:
            // *can this row produce a tone?* (♪ vs +) and *is it the
            // active row?* (Gold vs GoldDim). A faint ♪ on an inactive
            // playable row is the up-front signal — without it, "+"
            // reads as expand-only and the audio is a hidden feature.
            // Sub-audible / non-numeric rows keep "+" since there is
            // genuinely nothing to hear.
            val glyph = when {
                active && playable -> "♪"
                active             -> "−"
                playable           -> "♪"
                else               -> "+"
            }
            Text(
                text = glyph,
                color = if (active) Gold else GoldDim,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.width(20.dp),
            )
        }
        AnimatedVisibility(
            visible = active,
            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
        ) {
            // Expanded body uses the full row width — indenting to the title
            // column crushed body text into a 60-percent gutter on a phone
            // and made the five sections hard to read. The HairlineDivider
            // below the row brackets the detail visually instead.
            //
            // RECORDINGS sits up top when the entry has any — leading the
            // expansion with curated artistic compositions matches the dial,
            // which surfaces work + performer as its primary content. Bands
            // with no genuine pre-electronic lineage (Schumann harmonics,
            // brainwave bands, numerology) skip this section entirely.
            Column(modifier = Modifier.padding(top = 18.dp, bottom = 6.dp)) {
                if (entry.compositions.isNotEmpty()) {
                    CompositionsSection(entry.compositions)
                }
                EntrySection("HISTORY", entry.history)
                EntrySection("HISTORICAL USES", entry.uses)
                EntrySection("SCIENTIFIC STUDIES", entry.studies)
                EntrySection("REFERENCES", entry.references)
                EntrySection("WHEN TO USE", entry.usage, isLast = true)
            }
        }
    }
    HairlineDivider()
}

@Composable
private fun CompositionsSection(compositions: List<Composition>) {
    // Each composition renders as work (MuteSoft, prominent) + performer
    // (Mute, smaller) — the same pairing the dial uses in its now-playing
    // caption, so the eye reads continuity between the two modes. Stacked
    // left-aligned with a tight inner spacer; the section trailing spacer
    // matches EntrySection so the rhythm of the expanded panel stays even.
    Text(
        text = "RECORDINGS",
        color = Gold,
        fontSize = 11.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(10.dp))
    compositions.forEachIndexed { index, c ->
        Text(
            text = c.work,
            color = MuteSoft,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Light,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = c.performer,
            color = Mute,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Light,
        )
        if (index < compositions.lastIndex) Spacer(Modifier.height(10.dp))
    }
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun EntrySection(label: String, body: String, isLast: Boolean = false) {
    Text(
        text = label,
        color = Gold,
        fontSize = 11.sp,
        letterSpacing = 2.5.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(7.dp))
    Text(
        text = body,
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Light,
    )
    if (!isLast) Spacer(Modifier.height(20.dp))
}
