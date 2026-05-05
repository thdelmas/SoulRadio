package com.soulradio.soulradio

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
internal fun LibraryScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf(UserTracksStore.all(context)) }
    var importing by remember { mutableStateOf(false) }
    // Per-track ghost-row tracking: a Uri whose file has been deleted /
    // moved / had permission revoked. Re-checked whenever the track list
    // changes, off the main thread. Until the result lands, rows render
    // as healthy by default — better than flashing "missing" then correcting.
    val missingIds = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(tracks) {
        withContext(Dispatchers.IO) {
            for (t in tracks) {
                missingIds[t.id] = !UriHealth.isReadable(context, t.sourceUri)
            }
        }
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            importing = true
            val track = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                }
                doImport(context, uri)
            }
            if (track != null) {
                tracks = UserTracksStore.add(context, track)
            }
            importing = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Bg).padding(horizontal = 24.dp),
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
            text = "library",
            color = Gold,
            fontSize = 14.sp,
            letterSpacing = 3.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "your imports, profiled by signal — descriptive only.",
            color = MuteSoft,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(20.dp))
        HairlineDivider()
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (importing) "profiling…" else "add file",
                color = if (importing) GoldDim else Gold,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, GoldDim, CircleShape)
                    .clickable(enabled = !importing) {
                        picker.launch(arrayOf("audio/*"))
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.height(16.dp))

        if (tracks.isEmpty()) {
            Text(
                text = "no imports yet.",
                color = MuteSoft,
                fontSize = 11.sp,
            )
        } else {
            TrackList(
                tracks = tracks,
                isMissing = { id -> missingIds[id] == true },
                onChangeBands = { id, newBands ->
                    tracks = UserTracksStore.update(context, id) { t ->
                        t.copy(assignedBands = newBands, manualOverride = true)
                    }
                },
                onRemove = { id ->
                    tracks = UserTracksStore.remove(context, id)
                    missingIds.remove(id)
                },
            )
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<UserTrack>,
    isMissing: (String) -> Boolean,
    onChangeBands: (id: String, newBands: Set<String>) -> Unit,
    onRemove: (id: String) -> Unit,
) {
    LazyColumn {
        // Group by primary assigned band (first), with "—" for tracks the
        // listener has not filed yet (the ancestral / signals-only case).
        val grouped = tracks.groupBy { it.assignedBands.firstOrNull() ?: "—" }
        val keys = grouped.keys.toList().sortedWith(compareBy { groupOrder(it) })
        for (band in keys) {
            val group = grouped[band] ?: continue
            item(key = "h-$band") {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (band == "—") "signals only · file manually" else "$band Hz",
                    color = Gold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(6.dp))
            }
            items(group, key = { it.id }) { track ->
                TrackRow(
                    track = track,
                    missing = isMissing(track.id),
                    onAssignmentChange = { onChangeBands(track.id, it) },
                    onRemove = { onRemove(track.id) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun groupOrder(band: String): Int = when (band) {
    "—" -> 1000 // ungrouped at the bottom
    else -> ALL_BANDS.indexOf(band).let { if (it < 0) 999 else it }
}

@Composable
private fun TrackRow(
    track: UserTrack,
    missing: Boolean,
    onAssignmentChange: (Set<String>) -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by remember(track.id) { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        ) {
            Text(
                text = track.displayName,
                color = if (missing) Mute else MuteSoft,
                fontStyle = if (missing) FontStyle.Italic else FontStyle.Normal,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (expanded) "−" else "+",
                color = GoldDim,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        SignalChips(track.profile.signals, missing)
        if (expanded) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "file under (toggle any — empty means signals only):",
                color = MuteSoft,
                fontSize = 10.sp,
            )
            Spacer(Modifier.height(6.dp))
            BandChooser(track.assignedBands, onAssignmentChange)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "remove",
                color = GoldDim,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onRemove() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SignalChips(s: ProfileSignals, missing: Boolean = false) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        if (missing) {
            // Leading chip so it's the first thing the listener reads on
            // a ghost row. Mute colour, no border emphasis — the chip
            // describes a state, not an alarm.
            Chip("missing · file unreachable", color = Mute)
        }
        s.dominantHz?.let { Chip("${it.toInt()} Hz") }
        s.bpm?.let { Chip("${it.toInt()} BPM") }
        Chip(tiltLabel(s.spectralTiltDbPerOctave))
        if (s.sub60HzEnergyFraction > 0.3f) {
            Chip("sub-60 ${(s.sub60HzEnergyFraction * 100).toInt()}%")
        }
    }
}

@Composable
private fun Chip(text: String, color: Color = MuteSoft) {
    Text(
        text = text,
        color = color,
        fontSize = 10.sp,
        modifier = Modifier
            .padding(end = 6.dp)
            .clip(CircleShape)
            .border(1.dp, Hairline, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

private fun tiltLabel(slope: Float): String = when {
    slope < -4.5f -> "brown"
    slope < -1.5f -> "pink"
    slope < 1.5f -> "white"
    else -> "bright"
}

private val ALL_BANDS: List<String> = Frequencies.all.map { it.key }

@Composable
private fun BandChooser(assigned: Set<String>, onChange: (Set<String>) -> Unit) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        for (band in ALL_BANDS) {
            val selected = band in assigned
            Text(
                text = band,
                color = if (selected) Gold else GoldDim,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(CircleShape)
                    .border(1.dp, if (selected) Gold else GoldDim, CircleShape)
                    .clickable {
                        val next = if (selected) assigned - band else assigned + band
                        onChange(next)
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

private fun doImport(context: Context, uri: Uri): UserTrack? {
    val pcm = Decoder.decode(context, uri) ?: return null
    val assignment = AudioProfiler.profile(pcm.samples, pcm.channels, pcm.sampleRate)
    val displayName = queryDisplayName(context, uri)
        ?: uri.lastPathSegment
        ?: "imported track"
    val initialBands = assignment.matches.firstOrNull()?.let { setOf(it.bandKey) } ?: emptySet()
    return UserTrack(
        id = UUID.randomUUID().toString(),
        sourceUri = uri.toString(),
        displayName = displayName,
        addedAtMs = System.currentTimeMillis(),
        profile = assignment,
        assignedBands = initialBands,
        manualOverride = false,
    )
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    return context.contentResolver.query(
        uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null,
    )?.use { c ->
        if (c.moveToFirst()) c.getString(0) else null
    }
}
