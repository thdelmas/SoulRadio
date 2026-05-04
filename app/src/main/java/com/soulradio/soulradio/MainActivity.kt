package com.soulradio.soulradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface as M3Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

private enum class AppSurface { Main, Notes, Radio, Settings }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        PlaybackService.startIfFirstLaunch(this)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                M3Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
                    val context = LocalContext.current
                    // Screen dim follows the band's time-of-day, not the
                    // user's selection — at 3am the room is dim whether the
                    // radio is on AUTO, paused, or holding a tapped tone.
                    // Re-poll every 5 min so the dim follows band crossings
                    // without holding a precise boundary timer.
                    var dayBand by remember {
                        mutableStateOf(Frequencies.forNow(context))
                    }
                    LaunchedEffect(Unit) {
                        while (true) {
                            dayBand = Frequencies.forNow(context)
                            delay(5 * 60_000L)
                        }
                    }
                    val dim by animateFloatAsState(
                        targetValue = screenDimFor(dayBand.key),
                        animationSpec = tween(durationMillis = 5000),
                        label = "screen-dim",
                    )
                    var surface by remember { mutableStateOf(AppSurface.Main) }
                    // The device back gesture should return to the main
                    // screen when a secondary surface is open; otherwise it
                    // falls through to default (finish the activity), which
                    // is what one expects on the root.
                    BackHandler(enabled = surface != AppSurface.Main) {
                        surface = AppSurface.Main
                    }
                    Crossfade(
                        targetState = surface,
                        animationSpec = tween(500),
                        modifier = Modifier.alpha(dim),
                        label = "screen",
                    ) { current ->
                        when (current) {
                            AppSurface.Main -> MainScreen(
                                onOpenNotes = { surface = AppSurface.Notes },
                                onOpenRadio = { surface = AppSurface.Radio },
                                onOpenSettings = { surface = AppSurface.Settings },
                            )
                            AppSurface.Notes -> AboutScreen(
                                onClose = { surface = AppSurface.Main },
                            )
                            AppSurface.Radio -> RadioModeScreen(
                                onClose = { surface = AppSurface.Main },
                            )
                            AppSurface.Settings -> SettingsScreen(
                                onClose = { surface = AppSurface.Main },
                            )
                        }
                    }
                }
            }
        }
    }
}
