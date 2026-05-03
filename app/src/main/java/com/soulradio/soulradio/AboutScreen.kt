package com.soulradio.soulradio

import android.content.Context
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
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
    val body = remember(current.key) { readAsset(context, current.asset) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "close",
                color = GoldDim,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onClose() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        TabRow(current = current.key, onSelect = { key -> current = DOCS.first { it.key == key } })
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Hairline),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp),
        ) {
            MarkdownBody(body)
            Spacer(Modifier.height(48.dp))
        }
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
            Text(
                text = doc.tab,
                color = if (active) Gold else GoldDim,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = if (active) FontWeight.Medium else FontWeight.Light,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelect(doc.key) }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun MarkdownBody(text: String) {
    val lines = text.lines()
    var i = 0
    while (i < lines.size) {
        val raw = lines[i]
        val line = raw.trimEnd()
        when {
            line.isBlank() -> Spacer(Modifier.height(8.dp))
            line.startsWith("# ") -> Heading(line.removePrefix("# "), 22.sp, FontWeight.Medium, Gold, top = 12.dp)
            line.startsWith("## ") -> Heading(line.removePrefix("## "), 17.sp, FontWeight.Medium, Gold, top = 18.dp)
            line.startsWith("### ") -> Heading(line.removePrefix("### "), 14.sp, FontWeight.Medium, GoldDim, top = 12.dp)
            line.startsWith("- ") -> Bullet(strip(line.removePrefix("- ")))
            line.startsWith("* ") -> Bullet(strip(line.removePrefix("* ")))
            line.matches(Regex("^\\d+\\.\\s.*")) -> Bullet(strip(line.replaceFirst(Regex("^\\d+\\.\\s"), "")))
            line.startsWith("> ") -> Quote(strip(line.removePrefix("> ")))
            line.startsWith("---") -> Spacer(Modifier.height(4.dp))
            line.startsWith("|") -> Unit // tables aren't rendered specially — fall through skips them
            else -> Paragraph(strip(line))
        }
        i++
    }
}

@Composable
private fun Heading(
    text: String,
    size: androidx.compose.ui.unit.TextUnit,
    weight: FontWeight,
    color: androidx.compose.ui.graphics.Color,
    top: androidx.compose.ui.unit.Dp,
) {
    Spacer(Modifier.height(top))
    Text(
        text = strip(text),
        color = color,
        fontSize = size,
        fontWeight = weight,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun Paragraph(text: String) {
    Text(
        text = text,
        color = MuteSoft,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun Bullet(text: String) {
    Row(modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp)) {
        Text(text = "·", color = GoldDim, fontSize = 13.sp, modifier = Modifier.padding(end = 10.dp))
        Text(
            text = text,
            color = MuteSoft,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
private fun Quote(text: String) {
    Text(
        text = text,
        color = Mute,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Light,
        modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
    )
}

// Strips the most common inline markdown markers without trying to be a full
// parser — bold/italic/links render as their text content. Anything fancier
// belongs in a real markdown library, which the project intentionally avoids.
private fun strip(s: String): String {
    var out = s
    out = out.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
    out = out.replace(Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)"), "$1")
    out = out.replace(Regex("_(.+?)_"), "$1")
    out = out.replace(Regex("`([^`]+)`"), "$1")
    out = out.replace(Regex("\\[([^\\]]+)\\]\\([^)]*\\)"), "$1")
    return out
}

private fun readAsset(context: Context, path: String): String =
    runCatching {
        context.assets.open(path).bufferedReader().use { it.readText() }
    }.getOrElse { "(missing: $path)" }
