package com.soulradio.soulradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

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
                    // radio is on dj, paused, or holding a tapped tone.
                    // Re-poll every 5 min so the dim follows band crossings
                    // without holding a precise boundary timer.
                    var dayBand by remember {
                        mutableStateOf(Frequencies.forNow(context))
                    }
                    var hour by remember { mutableStateOf(Frequencies.currentHour()) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            dayBand = Frequencies.forNow(context)
                            hour = Frequencies.currentHour()
                            delay(5 * 60_000L)
                        }
                    }
                    val dim by animateFloatAsState(
                        targetValue = screenDimFor(dayBand.key),
                        animationSpec = tween(durationMillis = 5000),
                        label = "screen-dim",
                    )
                    var surface by remember { mutableStateOf(AppSurface.Main) }
                    // Single source of truth for the auto-loop state. Lifted
                    // here so the [ModeStrip] (which sits above the surface
                    // Crossfade) and [MainScreen] (which still owns the
                    // tap-pauses-auto interaction) read and write the same
                    // value. PlaybackService is the persistent backing store;
                    // this state mirrors it for Compose recomposition.
                    var autoOn by remember {
                        mutableStateOf(PlaybackService.isAutoEnabled(context))
                    }
                    val setAuto: (Boolean) -> Unit = { value ->
                        autoOn = value
                        PlaybackService.setAuto(context, value)
                    }
                    // Bumped only when the listener presses the `dial` pill
                    // in the strip. MainScreen observes this counter and
                    // clears its selection on increment — restoring the
                    // explicit "silence + dial mode" intent the old AUTO
                    // toggle carried. A side-effect `setAuto(false)` from a
                    // dial tap does *not* increment, so the just-tapped
                    // tone keeps playing.
                    var dialPillTrigger by remember { mutableStateOf(0) }

                    // Device back returns to Main from any non-Main surface;
                    // Chakra is the one exception — it's reached from Body
                    // (not the strip) so back returns to Body to match the
                    // entry path. Root-level back falls through to finish.
                    BackHandler(enabled = surface != AppSurface.Main) {
                        surface = if (surface == AppSurface.Chakra) {
                            AppSurface.Body
                        } else {
                            AppSurface.Main
                        }
                    }

                    val isMode = surface == AppSurface.Main ||
                        surface == AppSurface.Radio ||
                        surface == AppSurface.Body ||
                        surface == AppSurface.Chakra ||
                        surface == AppSurface.Library

                    // Parent Column owns the system-bar inset and the dim;
                    // child screens drop their own systemBarsPadding and dim
                    // so things don't double up. Each screen keeps its own
                    // horizontal padding (24 dp) — the strip is allowed
                    // tighter horizontal so its pills cluster cleanly.
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .alpha(dim),
                    ) {
                        AnimatedVisibility(
                            visible = isMode,
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(200)),
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                                Spacer(Modifier.height(12.dp))
                                ModeStrip(
                                    currentSurface = surface,
                                    autoOn = autoOn,
                                    currentHour = hour,
                                    onDjMode = {
                                        setAuto(true)
                                        if (surface != AppSurface.Main) surface = AppSurface.Main
                                    },
                                    onDialMode = {
                                        setAuto(false)
                                        if (surface != AppSurface.Main) surface = AppSurface.Main
                                        dialPillTrigger++
                                    },
                                    onRadio = { surface = AppSurface.Radio },
                                    onBody = { surface = AppSurface.Body },
                                    onLibrary = { surface = AppSurface.Library },
                                    onOpenSettings = { surface = AppSurface.Settings },
                                    onOpenNotes = { surface = AppSurface.Notes },
                                )
                            }
                        }
                        Crossfade(
                            targetState = surface,
                            animationSpec = tween(500),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            label = "screen",
                        ) { current ->
                            when (current) {
                                AppSurface.Main -> MainScreen(
                                    autoOn = autoOn,
                                    onSetAuto = setAuto,
                                    dialPillTrigger = dialPillTrigger,
                                )
                                AppSurface.Notes -> AboutScreen(
                                    onClose = { surface = AppSurface.Main },
                                )
                                AppSurface.Radio -> RadioModeScreen()
                                AppSurface.Body -> BodyScreen(
                                    onOpenChakra = { surface = AppSurface.Chakra },
                                )
                                AppSurface.Chakra -> ChakraScreen()
                                AppSurface.Library -> LibraryScreen(
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
}
