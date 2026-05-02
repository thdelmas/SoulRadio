package com.soulradio.soulradio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

private val Bg = Color.Black
private val Gold = Color(0xFFD4AF37)
private val GoldDim = Color(0xFF6E5A1C)
private val Slate = Color(0xFF1A1A1A)
private val SlateDeep = Color(0xFF0E0E0E)
private val Mute = Color(0xFF555555)
private val MuteSoft = Color(0xFF888888)
private val Hairline = Color(0xFF222222)

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maybeRequestNotificationPermission()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
                    RadioScreen()
                }
            }
        }
    }

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
private fun RadioScreen() {
    val context = LocalContext.current
    val engine = remember { TrackEngine(context) }
    var selected by remember { mutableStateOf<Frequency?>(null) }
    var auto by remember { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { engine.release() } }

    LaunchedEffect(auto) {
        if (!auto) return@LaunchedEffect
        while (true) {
            val target = Frequencies.forNow()
            if (selected?.key != target.key) {
                selected = target
                engine.selectFrequency(target)
            }
            delay(60_000)
        }
    }

    val onTap: (Frequency) -> Unit = { freq ->
        auto = false
        if (selected?.key == freq.key) {
            selected = null
            engine.selectFrequency(null)
        } else {
            selected = freq
            engine.selectFrequency(freq)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        AutoPill(
            auto = auto,
            onToggle = {
                val next = !auto
                auto = next
                if (!next) {
                    selected = null
                    engine.selectFrequency(null)
                }
            },
        )

        Spacer(Modifier.weight(1f))

        Dial(
            selectedKey = selected?.key,
            tunedKeys = engine.tunedKeys,
            onTap = onTap,
        )

        Spacer(Modifier.height(36.dp))
        Divider()
        Spacer(Modifier.height(24.dp))

        CompanionsRow(
            selectedKey = selected?.key,
            tunedKeys = engine.tunedKeys,
            onTap = onTap,
        )

        Spacer(Modifier.weight(1f))

        Caption(
            selected = selected,
            isTuned = selected?.let { engine.isTuned(it) } ?: false,
        )

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun AutoPill(auto: Boolean, onToggle: () -> Unit) {
    val color = if (auto) Gold else MuteSoft
    val text = if (auto) {
        "AUTO · ${Frequencies.currentHour().toString().padStart(2, '0')}h"
    } else "auto"
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = if (auto) FontWeight.Medium else FontWeight.Light,
        letterSpacing = 3.sp,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onToggle() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
    )
}

@Composable
private fun Dial(
    selectedKey: String?,
    tunedKeys: Set<String>,
    onTap: (Frequency) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Frequencies.dial.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                row.forEach { freq ->
                    DialNode(
                        freq = freq,
                        selected = selectedKey == freq.key,
                        tuned = freq.key in tunedKeys,
                        onTap = onTap,
                    )
                }
            }
        }
    }
}

@Composable
private fun DialNode(
    freq: Frequency,
    selected: Boolean,
    tuned: Boolean,
    onTap: (Frequency) -> Unit,
) {
    val (bg, fg) = when {
        selected -> Gold to Color.Black
        tuned    -> Slate to Gold
        else     -> SlateDeep to GoldDim
    }
    Box(
        modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(bg)
            .let {
                if (selected) it
                else it.border(1.dp, if (tuned) Gold else GoldDim, CircleShape)
            }
            .clickable { onTap(freq) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = freq.label,
            color = fg,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
private fun CompanionsRow(
    selectedKey: String?,
    tunedKeys: Set<String>,
    onTap: (Frequency) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Frequencies.companions.forEach { freq ->
            CompanionNode(
                freq = freq,
                selected = selectedKey == freq.key,
                tuned = freq.key in tunedKeys,
                onTap = onTap,
            )
        }
    }
}

@Composable
private fun CompanionNode(
    freq: Frequency,
    selected: Boolean,
    tuned: Boolean,
    onTap: (Frequency) -> Unit,
) {
    val (bg, fg) = when {
        selected -> Gold to Color.Black
        tuned    -> Slate to Gold
        else     -> SlateDeep to GoldDim
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(bg)
                .let {
                    if (selected) it
                    else it.border(1.dp, if (tuned) Gold else GoldDim, CircleShape)
                }
                .clickable { onTap(freq) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = freq.label,
                color = fg,
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = freq.title,
            color = MuteSoft,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .height(1.dp)
            .background(Hairline),
    )
}

@Composable
private fun Caption(selected: Frequency?, isTuned: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (selected == null) {
            Text(
                text = "tap a tone · or leave it",
                color = Mute,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
            )
            return
        }
        Text(
            text = selected.title,
            color = Gold,
            fontSize = 13.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Light,
        )
        Spacer(Modifier.height(10.dp))
        if (!isTuned) {
            Text(
                text = "untuned · recording forthcoming",
                color = Mute,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            )
        } else {
            selected.nowPlaying?.let { np ->
                Text(
                    text = np.work,
                    color = MuteSoft,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = np.performer,
                    color = Mute,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
