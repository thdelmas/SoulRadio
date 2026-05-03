package com.soulradio.soulradio

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Edit this URL when the donation page moves. The app never communicates
// with the donation platform — it only hands off to the system browser.
// See docs/contribution-popup.md (or the portfolio guide it references)
// for the compliance reasoning: a non-Billing donation may not grant any
// in-app benefit, so the app must remain ignorant of who has donated.
private const val DONATE_URL = "https://theophile.world/sponsor"

// A real address the dev reads. Every feedback message gets a human
// reply — that is the highest-leverage community-building action in the
// whole popup.
private const val FEEDBACK_EMAIL = "contact@theophile.world"

/**
 * The monthly community-building popup. Three honest action paths plus a
 * dismiss; no donor tracking, no feature gates, no donor-vs-non-donor
 * differentiation. The dialog never pauses or ducks audio — if AUTO is
 * already running underneath, it keeps running.
 *
 * The caller is responsible for marking the popup shown in
 * [ContributionStore] before rendering — that resets the cadence whether
 * the user actions it or dismisses it.
 */
@Composable
fun ContributionPopup(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "made by one person",
                color = Gold,
                fontSize = 13.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        text = {
            Text(
                text = "Three ways you can help keep this radio on the air.",
                color = Mute,
                fontSize = 13.sp,
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                ActionButton(label = "donate") {
                    openUrl(context, DONATE_URL)
                    onDismiss()
                }
                ActionButton(label = "leave a review") {
                    openReview(context)
                    onDismiss()
                }
                ActionButton(label = "send feedback") {
                    openFeedback(context)
                    onDismiss()
                }
                Spacer(Modifier.height(4.dp))
                ActionButton(label = "maybe later", subdued = true) { onDismiss() }
            }
        },
        containerColor = Bg,
    )
}

@Composable
private fun ActionButton(
    label: String,
    subdued: Boolean = false,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
    ) {
        Text(
            text = label,
            color = if (subdued) Mute else Gold,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontWeight = if (subdued) FontWeight.Light else FontWeight.Medium,
        )
    }
}

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

private fun openReview(context: Context) {
    // Try the Play Store deep link first; fall back to the web listing if
    // the Play Store app is not installed (sideloaded / F-Droid builds).
    val pkg = context.packageName
    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(market)
    } catch (_: ActivityNotFoundException) {
        openUrl(context, "https://play.google.com/store/apps/details?id=$pkg")
    }
}

private fun openFeedback(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$FEEDBACK_EMAIL")
        putExtra(Intent.EXTRA_SUBJECT, "SoulRadio feedback")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}
