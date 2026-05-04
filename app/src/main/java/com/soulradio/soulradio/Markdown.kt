package com.soulradio.soulradio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The reader is its own register. The dial is a control surface — short
// labels, sans, generous letter-spacing. The notes are a text — long
// paragraphs, mixed weights, a quote here and a list there. Switching
// the body to a serif marks that change without adding chrome.
private val ReaderBody = FontFamily.Serif

/**
 * Tiny CommonMark-ish renderer for the bundled docs. Intentionally not a
 * full parser — supports the subset the docs actually use: ATX headings,
 * blockquotes, ordered/unordered lists, GFM tables, hairline rules, and
 * inline `**bold**`, `*italic*`, `` `code` ``, `[text](url)` links.
 *
 * Anything fancier belongs in a real markdown library, which the project
 * intentionally avoids — see [docs/licensing.md](docs/licensing.md) and
 * the dependency policy in [CLAUDE.md](../../../../../../CLAUDE.md).
 */
@Composable
internal fun MarkdownBody(text: String) {
    val lines = text.lines()
    var i = 0
    while (i < lines.size) {
        val line = lines[i].trimEnd()
        when {
            line.isBlank() -> Spacer(Modifier.height(8.dp))
            line.startsWith("# ") -> Heading(line.removePrefix("# "), 26.sp, FontWeight.Light, Gold, top = 12.dp, letterSpacing = 1.5.sp)
            line.startsWith("## ") -> Heading(line.removePrefix("## "), 20.sp, FontWeight.Medium, Gold, top = 22.dp, letterSpacing = 1.sp)
            line.startsWith("### ") -> Heading(line.removePrefix("### "), 16.sp, FontWeight.Medium, GoldDim, top = 16.dp, letterSpacing = 1.sp)
            line.startsWith("- ") -> Bullet(line.removePrefix("- "))
            line.startsWith("* ") -> Bullet(line.removePrefix("* "))
            NUMBERED.matches(line) -> {
                val m = NUMBERED.matchEntire(line)!!
                NumberedItem(m.groupValues[1], m.groupValues[2])
            }
            line.startsWith("> ") -> Quote(line.removePrefix("> "))
            line.startsWith("---") -> Rule()
            line.startsWith("|") -> {
                val block = mutableListOf<String>()
                while (i < lines.size && lines[i].trimEnd().startsWith("|")) {
                    block += lines[i].trimEnd()
                    i++
                }
                Table(parseTable(block))
                continue
            }
            else -> Paragraph(line)
        }
        i++
    }
}

private val NUMBERED = Regex("^(\\d+)\\.\\s(.*)")

private fun parseTable(rows: List<String>): List<List<String>> =
    rows
        .map { row -> row.trim().trim('|').split('|').map { it.trim() } }
        .filter { cells -> cells.any { !it.matches(Regex("^[\\s\\-:]*$")) } }

@Composable
private fun Table(rows: List<List<String>>) {
    if (rows.isEmpty()) return
    val columnCount = rows.maxOf { it.size }
    Spacer(Modifier.height(6.dp))
    rows.forEachIndexed { index, cells ->
        val isHeader = index == 0
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            (0 until columnCount).forEach { col ->
                val cell = cells.getOrNull(col).orEmpty()
                Text(
                    text = inlineMd(cell),
                    color = if (isHeader) GoldDim else MuteSoft,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontFamily = ReaderBody,
                    fontWeight = if (isHeader) FontWeight.Medium else FontWeight.Light,
                    modifier = Modifier
                        .weight(columnWeight(col, columnCount))
                        .padding(end = 10.dp),
                )
            }
        }
        if (isHeader) HairlineRow()
    }
    Spacer(Modifier.height(8.dp))
}

// FREQUENCIES.md's quick-map is the only table the reader sees today —
// "Hour band · Tone · Intention". Equal weights squashed the leftmost
// time range into two lines while "Tone" sat with empty space; this map
// gives Hz a tighter slot and the prose column the room it needs.
private fun columnWeight(col: Int, total: Int): Float =
    if (total == 3) when (col) { 0 -> 1.2f; 1 -> 0.7f; else -> 1.7f } else 1f

@Composable
private fun HairlineRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Hairline),
    )
}

@Composable
private fun Rule() {
    Spacer(Modifier.height(14.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth(0.32f)
            .height(1.dp)
            .background(Hairline),
    )
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun Heading(
    text: String,
    size: TextUnit,
    weight: FontWeight,
    color: Color,
    top: Dp,
    letterSpacing: TextUnit,
) {
    Spacer(Modifier.height(top))
    Text(
        text = inlineMd(text),
        color = color,
        fontSize = size,
        fontWeight = weight,
        fontFamily = ReaderBody,
        letterSpacing = letterSpacing,
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun Paragraph(text: String) {
    Text(
        text = inlineMd(text),
        color = MuteSoft,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        fontFamily = ReaderBody,
        fontWeight = FontWeight.Light,
    )
}

@Composable
private fun Bullet(text: String) {
    Row(modifier = Modifier.padding(start = 4.dp, top = 3.dp, bottom = 3.dp)) {
        Text(
            text = "·",
            color = GoldDim,
            fontSize = 15.sp,
            fontFamily = ReaderBody,
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = inlineMd(text),
            color = MuteSoft,
            fontSize = 15.sp,
            lineHeight = 23.sp,
            fontFamily = ReaderBody,
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
private fun NumberedItem(num: String, text: String) {
    Row(modifier = Modifier.padding(start = 2.dp, top = 4.dp, bottom = 4.dp)) {
        Text(
            text = "$num.",
            color = GoldDim,
            fontSize = 14.sp,
            fontFamily = ReaderBody,
            textAlign = TextAlign.End,
            modifier = Modifier
                .width(24.dp)
                .padding(end = 10.dp),
        )
        Text(
            text = inlineMd(text),
            color = MuteSoft,
            fontSize = 15.sp,
            lineHeight = 23.sp,
            fontFamily = ReaderBody,
            fontWeight = FontWeight.Light,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun Quote(text: String) {
    // Gold left rule + serif body. The bar carries across wrapped lines
    // because the Row's height is sized to the text via IntrinsicSize.Min.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 6.dp, bottom = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(GoldDim),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = inlineMd(text),
            color = MuteSoft,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontFamily = ReaderBody,
            fontWeight = FontWeight.Light,
        )
    }
}

// Minimal inline pass: **bold**, *italic*, `code`, [text](url). Walks the
// string once and emits styled spans into an AnnotatedString. Anything
// unrecognised passes through unchanged — the goal is a comfortable read,
// not a faithful CommonMark implementation.
private fun inlineMd(text: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    val s = text
    while (i < s.length) {
        when {
            s.startsWith("**", i) -> {
                val end = s.indexOf("**", i + 2)
                if (end == -1) { append(s[i]); i++ } else {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Medium, color = MuteSoft))
                    append(inlineMd(s.substring(i + 2, end)))
                    pop()
                    i = end + 2
                }
            }
            s[i] == '*' && i + 1 < s.length && s[i + 1] != '*' -> {
                val end = findUnescaped(s, '*', i + 1)
                if (end == -1) { append(s[i]); i++ } else {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(s.substring(i + 1, end))
                    pop()
                    i = end + 1
                }
            }
            s[i] == '`' -> {
                val end = s.indexOf('`', i + 1)
                if (end == -1) { append(s[i]); i++ } else {
                    pushStyle(SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = GoldDim,
                        fontSize = 13.sp,
                    ))
                    append(s.substring(i + 1, end))
                    pop()
                    i = end + 1
                }
            }
            s[i] == '[' -> {
                // [text](url): keep the visible text in a slightly accented
                // colour, drop the URL. The reader is offline; clickable
                // links would only deepen the surface, which the Notes
                // screen is deliberately trying not to do.
                val close = s.indexOf("](", i)
                val parenEnd = if (close != -1) s.indexOf(')', close + 2) else -1
                if (close == -1 || parenEnd == -1) { append(s[i]); i++ } else {
                    pushStyle(SpanStyle(color = GoldDim))
                    append(s.substring(i + 1, close))
                    pop()
                    i = parenEnd + 1
                }
            }
            else -> { append(s[i]); i++ }
        }
    }
}

private fun findUnescaped(s: String, ch: Char, from: Int): Int {
    var j = from
    while (j < s.length) {
        if (s[j] == '\\' && j + 1 < s.length) { j += 2; continue }
        if (s[j] == ch) return j
        j++
    }
    return -1
}
