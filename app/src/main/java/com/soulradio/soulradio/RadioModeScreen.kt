package com.soulradio.soulradio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Radio mode — the wider field, behind a deliberate door. Lists the
 * frequencies documented in `docs/tunables.md` that did *not* earn a place
 * on the dial. Tap a row to read why; if the row's Hz is audible (≥ 20 Hz),
 * the same tap also plays the tone as a sine demo. Sub-audible and
 * non-numeric rows expand the rationale only — there is nothing to hear.
 *
 * Per [MANIFESTO.md](../../../../../../MANIFESTO.md): exploration is opt-in,
 * never the default surface, never bleeds into the room. The sine demo
 * stops the moment the screen closes (DisposableEffect), so the radio
 * cannot leak its exploration audio into the auto loop or dial context.
 */
@Composable
fun RadioModeScreen(onClose: () -> Unit) {
    val scrollState = rememberScrollState()
    val sineDemo = remember { SineDemo() }
    DisposableEffect(Unit) { onDispose { sineDemo.release() } }

    // Single-active-row model: tapping a row makes it the active one
    // (expanding rationale + starting tone if audible); tapping the same
    // row again clears active. Coupling expand and play to one state keeps
    // the audio model trivially correct — at most one tone, ever.
    var activeHz by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(activeHz) {
        // Run AudioTrack lifecycle off the main thread; start/stop
        // synchronise internally and may briefly block on thread join.
        withContext(Dispatchers.IO) {
            val hz = activeHz?.let { Catalogue.audibleHzFor(it) }
            if (hz != null) sineDemo.start(hz) else sineDemo.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Header(onClose = onClose)
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
private fun Header(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Vertical padding clears the 48 dp tap-target floor — same as
        // AboutScreen's close affordance.
        Text(
            text = "close",
            color = GoldDim,
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onClose() }
                .padding(horizontal = 12.dp, vertical = 16.dp),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "RADIO",
            color = Gold,
            fontSize = 11.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 4.dp),
        )
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
        text = "Frequencies considered for the dial that did not earn a slot. The dial stays small so the room can recede; this is where the rest of the landscape is documented. Tap a row to read why a frequency is here and not there — audible Hz also play as a tone demo.",
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
            // Active row gets a small tone glyph when it's audible — a
            // visual confirmation that "tap = read + listen." Inactive
            // playable rows show "+", inactive non-playable rows show "+",
            // expanded non-playable rows show "−". The glyph is the only
            // way the user knows up front which rows have audio.
            val glyph = when {
                active && playable -> "♪"
                active             -> "−"
                else               -> "+"
            }
            Text(
                text = glyph,
                color = if (active && playable) Gold else GoldDim,
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
            // Rationale indented to the title column so the eye reads it as
            // detail belonging to the row above, not a new top-level entry.
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Spacer(Modifier.width(84.dp))
                Text(
                    text = entry.rationale,
                    color = Mute,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
    HairlineDivider()
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
