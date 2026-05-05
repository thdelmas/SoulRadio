package com.soulradio.soulradio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Top-level surfaces. The strip-reachable mode surfaces — [Main] (dial),
 * [Radio], [Body], [Library] — are peers in the [ModeStrip]: tapping a
 * pill switches modes. `dj` and `dial` both map to [Main] and are
 * distinguished by the auto state, not by surface — they share the dial
 * visual.
 *
 * [Chakra] is a peer-class cartography surface (same screen-class as
 * [Body], no audio of its own) but is *not* on the strip — the strip is
 * already at its width budget on a 360 dp phone, and a sixth pill would
 * overflow. Reached instead via a "see also" link at the bottom of
 * [BodyScreen]: one click in, same architectural tier.
 *
 * [Notes] and [Settings] are utilities, not modes. The strip is hidden
 * while they are open and they keep their own close affordance.
 */
internal enum class AppSurface { Main, Radio, Body, Chakra, Library, Notes, Settings }

/**
 * Shared header across the four peer modes. Renders four lowercase pills
 * — `dj`, `dial`, `radio`, `body` — with a small dot under the active
 * label. Same indicator the NOTES tab row uses, so the visual language
 * across the app stays in one key.
 *
 * - **dj** — enables the auto loop and surfaces [AppSurface.Main]. When
 *   on, the pill suffixes the current hour so the listener can read when
 *   the schedule will roll over.
 * - **dial** — disables the auto loop, *clears any selected tone*, and
 *   surfaces [AppSurface.Main]. The clear is the difference between a
 *   side-effect `setAuto(false)` (a tap on the dial pauses DJ but keeps
 *   the just-tapped tone playing) and the explicit *intent* of pressing
 *   the dial pill itself, which is "silence + dial mode."
 * - **radio** — opens [AppSurface.Radio], the wider catalogue.
 * - **body** — opens [AppSurface.Body], the lever map: sound categorised
 *   by what the listener reaches for, not by name or Hz.
 * - **library** — opens [AppSurface.Library], the listener's imported
 *   audio, auto-profiled and grouped by band. The fourth mode; the
 *   listener-as-curator surface.
 *
 * Callbacks are *semantic* — the strip says what the listener pressed,
 * not what state to mutate. The activity composes the right cleanup
 * (clear-on-dial, no-clear-on-dj) without the strip having to know
 * about [TrackEngine] or selection state.
 *
 * The trailing [GearGlyph] (settings) and [BookGlyph] (notes) sit on the
 * end of the strip as small utilities. They are not modes; they are
 * doors that the strip provides a parking spot for so the corner of the
 * screen has a single navigation surface rather than two.
 */
@Composable
internal fun ModeStrip(
    currentSurface: AppSurface,
    autoOn: Boolean,
    currentHour: Int,
    onDjMode: () -> Unit,
    onDialMode: () -> Unit,
    onRadio: () -> Unit,
    onBody: () -> Unit,
    onLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotes: () -> Unit,
) {
    val activePill: String? = when {
        currentSurface == AppSurface.Main && autoOn -> "dj"
        currentSurface == AppSurface.Main -> "dial"
        currentSurface == AppSurface.Radio -> "radio"
        currentSurface == AppSurface.Body -> "body"
        currentSurface == AppSurface.Library -> "lib"
        else -> null
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ModePill(
            label = if (autoOn) "dj · ${currentHour.toString().padStart(2, '0')}h" else "dj",
            active = activePill == "dj",
            // Reserve space for the wider on-state ("dj · HHh") so toggling
            // auto doesn't push the other pills sideways.
            minWidth = 64.dp,
            onClick = onDjMode,
        )
        ModePill(label = "dial", active = activePill == "dial", onClick = onDialMode)
        ModePill(label = "radio", active = activePill == "radio", onClick = onRadio)
        ModePill(label = "body", active = activePill == "body", onClick = onBody)
        // Abbreviated to "lib" so the strip still fits five mode pills + two
        // utility icons on a 360 dp phone width.
        ModePill(label = "lib", active = activePill == "lib", onClick = onLibrary)
        Spacer(Modifier.weight(1f))
        IconDoor(onClick = onOpenSettings) { GearGlyph() }
        IconDoor(onClick = onOpenNotes) { BookGlyph() }
    }
}

@Composable
private fun ModePill(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    minWidth: Dp = Dp.Unspecified,
) {
    val color by animateColorAsState(
        targetValue = if (active) Gold else GoldDim,
        animationSpec = tween(500),
        label = "pill-color",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .widthIn(min = minWidth)
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
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
