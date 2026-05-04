package com.soulradio.soulradio

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Doc(val key: String, val tab: String, val asset: String)

private val DOCS = listOf(
    Doc("origins",     "origins",     "docs/ORIGINS.md"),
    Doc("manifesto",   "manifesto",   "docs/MANIFESTO.md"),
    Doc("frequencies", "frequencies", "docs/FREQUENCIES.md"),
    Doc("credits",     "credits",     "docs/CREDITS.md"),
)

@Composable
fun AboutScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    var current by remember { mutableStateOf(DOCS.first()) }

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
        TabRow(current = current.key, onSelect = { key -> current = DOCS.first { it.key == key } })
        Spacer(Modifier.height(14.dp))
        HairlineDivider()
        // Crossfade swaps the body when the tab changes — softer than the
        // instant snap of recomposing the whole column. Each panel keeps
        // its own ScrollState keyed on the doc, so jumping back to a tab
        // returns the doc to the top rather than restoring a stale scroll.
        Crossfade(
            targetState = current.key,
            animationSpec = tween(400),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "doc-body",
        ) { key ->
            val doc = DOCS.first { it.key == key }
            val body = remember(key) { readAsset(context, doc.asset) }
            val scrollState = remember(key) { ScrollState(0) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
            ) {
                MarkdownBody(body)
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun Header(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Vertical padding clears the 48 dp tap-target floor.
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
        // Mirrors the RADIO mark on RadioModeScreen — a small surface tag
        // so the listener knows which room they're standing in. Not a
        // back-target; the close affordance handles that.
        Text(
            text = "NOTES",
            color = Gold,
            fontSize = 11.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 4.dp),
        )
    }
}

@Composable
private fun TabRow(current: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DOCS.forEach { doc ->
            val active = doc.key == current
            // A 4 dp dot under the active label — quieter than an underline
            // and lighter than a pill border. The slot stays the same height
            // on inactive tabs (transparent dot) so swapping tabs doesn't
            // shift baselines as the eye moves across the row.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelect(doc.key) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            ) {
                Text(
                    text = doc.tab,
                    color = if (active) Gold else GoldDim,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    fontWeight = if (active) FontWeight.Medium else FontWeight.Light,
                )
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (active) Gold else Color.Transparent),
                )
            }
        }
    }
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

private fun readAsset(context: Context, path: String): String =
    runCatching {
        context.assets.open(path).bufferedReader().use { it.readText() }
    }.getOrElse { "(missing: $path)" }
