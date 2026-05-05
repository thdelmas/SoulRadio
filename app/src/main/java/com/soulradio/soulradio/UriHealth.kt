package com.soulradio.soulradio

import android.content.Context
import android.net.Uri

internal object UriHealth {

    fun isReadable(context: Context, uri: Uri): Boolean = runCatching {
        context.contentResolver.openInputStream(uri)?.use { }
        true
    }.getOrDefault(false)

    fun isReadable(context: Context, uriString: String): Boolean = runCatching {
        isReadable(context, Uri.parse(uriString))
    }.getOrDefault(false)
}
