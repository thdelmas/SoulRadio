package com.soulradio.soulradio

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
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
    // Each doc starts at the top — switching tabs is navigation, not a
    // bookmark restore. Keying on current.key gives a fresh scroll state
    // per doc; jumping back to a tab returns to the top.
    val scrollState = remember(current.key) { ScrollState(0) }

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
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 20.dp),
        ) {
            MarkdownBody(body)
            Spacer(Modifier.height(48.dp))
        }
        LocationKnob()
    }
}

/**
 * Bottom-of-screen settings row for the solar-aware schedule. When no
 * location is stored the loop falls back to the clock-hour bands; setting
 * one shifts the bands so dawn arrives at actual dawn. Lat/lon as decimal
 * degrees — east/north positive.
 */
@Composable
private fun LocationKnob() {
    val context = LocalContext.current
    var current by remember { mutableStateOf(LocationStore.get(context)) }
    var expanded by remember { mutableStateOf(false) }
    var latText by remember { mutableStateOf(current?.lat?.toString() ?: "") }
    var lonText by remember { mutableStateOf(current?.lon?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Hairline),
    )
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "the loop · " + (current?.let { "%.2f, %.2f".format(it.lat, it.lon) }
                    ?: "clock hours"),
                color = MuteSoft,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (expanded) "close" else "tune",
                color = Gold,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, GoldDim, CircleShape)
                    .clickable {
                        expanded = !expanded
                        error = null
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                MiniField(
                    value = latText,
                    onChange = { latText = it; error = null },
                    label = "lat",
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                MiniField(
                    value = lonText,
                    onChange = { lonText = it; error = null },
                    label = "lon",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                error?.let {
                    Text(
                        text = it,
                        color = MuteSoft,
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f),
                    )
                } ?: Spacer(Modifier.weight(1f))
                Text(
                    text = "clear",
                    color = GoldDim,
                    fontSize = 11.sp,
                    letterSpacing = 3.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            LocationStore.clear(context)
                            current = null
                            latText = ""
                            lonText = ""
                            error = null
                            expanded = false
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
                Text(
                    text = "save",
                    color = Gold,
                    fontSize = 11.sp,
                    letterSpacing = 3.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, GoldDim, CircleShape)
                        .clickable {
                            val lat = latText.toDoubleOrNull()
                            val lon = lonText.toDoubleOrNull()
                            val parsed = if (lat != null && lon != null) {
                                runCatching { LatLon(lat, lon) }.getOrNull()
                            } else null
                            if (parsed == null) {
                                error = "lat in [-90,90], lon in [-180,180]"
                            } else {
                                LocationStore.set(context, parsed)
                                current = parsed
                                error = null
                                expanded = false
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun MiniField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 10.sp, letterSpacing = 2.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = MuteSoft,
            fontSize = 14.sp,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Bg,
            unfocusedContainerColor = Bg,
            focusedTextColor = MuteSoft,
            unfocusedTextColor = MuteSoft,
            focusedBorderColor = Gold,
            unfocusedBorderColor = GoldDim,
            focusedLabelColor = Gold,
            unfocusedLabelColor = GoldDim,
            cursorColor = Gold,
        ),
        modifier = modifier,
    )
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
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = if (active) FontWeight.Medium else FontWeight.Light,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelect(doc.key) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
            line.startsWith("# ") -> Heading(line.removePrefix("# "), 26.sp, FontWeight.Medium, Gold, top = 12.dp)
            line.startsWith("## ") -> Heading(line.removePrefix("## "), 20.sp, FontWeight.Medium, Gold, top = 20.dp)
            line.startsWith("### ") -> Heading(line.removePrefix("### "), 16.sp, FontWeight.Medium, GoldDim, top = 14.dp)
            line.startsWith("- ") -> Bullet(strip(line.removePrefix("- ")))
            line.startsWith("* ") -> Bullet(strip(line.removePrefix("* ")))
            line.matches(Regex("^\\d+\\.\\s.*")) -> Bullet(strip(line.replaceFirst(Regex("^\\d+\\.\\s"), "")))
            line.startsWith("> ") -> Quote(strip(line.removePrefix("> ")))
            line.startsWith("---") -> Spacer(Modifier.height(4.dp))
            line.startsWith("|") -> {
                // Consume the contiguous table block — first row is the
                // header, the GFM separator (|---|---|) is dropped, the
                // rest are body rows. FREQUENCIES.md's quick-map is the
                // page worth opening notes for; rendering it matters.
                val block = mutableListOf<String>()
                while (i < lines.size && lines[i].trimEnd().startsWith("|")) {
                    block += lines[i].trimEnd()
                    i++
                }
                Table(parseTable(block))
                continue
            }
            else -> Paragraph(strip(line))
        }
        i++
    }
}

private fun parseTable(rows: List<String>): List<List<String>> =
    rows
        .map { row -> row.trim().trim('|').split('|').map { strip(it.trim()) } }
        .filter { cells -> cells.any { !it.matches(Regex("^[\\s\\-:]*$")) } }

@Composable
private fun Table(rows: List<List<String>>) {
    if (rows.isEmpty()) return
    val columnCount = rows.maxOf { it.size }
    Spacer(Modifier.height(4.dp))
    rows.forEachIndexed { index, cells ->
        val isHeader = index == 0
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
        ) {
            (0 until columnCount).forEach { col ->
                val cell = cells.getOrNull(col).orEmpty()
                Text(
                    text = cell,
                    color = if (isHeader) GoldDim else MuteSoft,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = if (isHeader) FontWeight.Medium else FontWeight.Light,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                )
            }
        }
        if (isHeader) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Hairline),
            )
        }
    }
    Spacer(Modifier.height(4.dp))
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
        fontSize = 15.sp,
        lineHeight = 23.sp,
    )
}

@Composable
private fun Bullet(text: String) {
    Row(modifier = Modifier.padding(start = 4.dp, top = 3.dp, bottom = 3.dp)) {
        Text(text = "·", color = GoldDim, fontSize = 15.sp, modifier = Modifier.padding(end = 10.dp))
        Text(
            text = text,
            color = MuteSoft,
            fontSize = 15.sp,
            lineHeight = 23.sp,
        )
    }
}

@Composable
private fun Quote(text: String) {
    Text(
        text = text,
        color = MuteSoft,
        fontSize = 14.sp,
        lineHeight = 21.sp,
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
