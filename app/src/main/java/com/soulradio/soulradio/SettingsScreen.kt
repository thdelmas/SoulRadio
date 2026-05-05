package com.soulradio.soulradio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Settings surface — the only door to user-tunable knobs. Today that's the
 * solar-aware schedule (lat/lon); future settings should land here, not in
 * the docs surface, so pedagogy stays separate from configuration.
 */
@Composable
fun SettingsScreen(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
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
                    .padding(horizontal = 12.dp, vertical = 16.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "settings",
            color = Gold,
            fontSize = 14.sp,
            letterSpacing = 3.sp,
        )
        Spacer(Modifier.height(20.dp))
        HairlineDivider()
        Spacer(Modifier.height(20.dp))
        LocationKnob()
        Spacer(Modifier.height(24.dp))
        LibrarySourceKnob()
        Spacer(Modifier.weight(1f))
    }
}

/**
 * The app-wide source filter governing which playlists feed the loop, dial,
 * and Radio: curated catalogue only, the listener's imports, or both
 * together. Defaults to APP_ONLY — the user library is opt-in.
 */
@Composable
private fun LibrarySourceKnob() {
    val context = LocalContext.current
    var current by remember { mutableStateOf(LibrarySourceStore.get(context)) }

    Column {
        Text(
            text = "the library · " + sourceLabel(current),
            color = MuteSoft,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(10.dp))
        Row {
            for (src in LibrarySource.values()) {
                val selected = src == current
                Text(
                    text = sourceLabel(src),
                    color = if (selected) Gold else GoldDim,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, if (selected) Gold else GoldDim, CircleShape)
                        .clickable {
                            LibrarySourceStore.set(context, src)
                            current = src
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

private fun sourceLabel(s: LibrarySource): String = when (s) {
    LibrarySource.APP_ONLY -> "app"
    LibrarySource.MIXED -> "mixed"
    LibrarySource.USER_ONLY -> "user"
}

/**
 * The solar-aware schedule. When no location is stored the loop falls back
 * to clock-hour bands; setting one shifts the bands so dawn arrives at
 * actual dawn. Lat/lon as decimal degrees — east/north positive.
 */
@Composable
private fun LocationKnob() {
    val context = LocalContext.current
    var current by remember { mutableStateOf(LocationStore.get(context)) }
    var expanded by remember { mutableStateOf(false) }
    var latText by remember { mutableStateOf(current?.lat?.toString() ?: "") }
    var lonText by remember { mutableStateOf(current?.lon?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Column {
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
