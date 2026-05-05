package com.soulradio.soulradio

import android.content.Context
import android.net.Uri

/**
 * Cheap reachability check for a content Uri — opens an input stream
 * and closes it immediately. Used by [TrackResolver] (skip missing
 * files in the playback path) and [LibraryScreen] (mark ghost entries
 * the listener can clean up).
 *
 * The user can delete or move a file after import: Downloads cleanup,
 * SD card swap, share-sheet permission revoked. Without this check, the
 * loop would queue dead URIs into ExoPlayer, the dial would tap into
 * silence, and the Library would carry ghost rows the listener cannot
 * diagnose.
 */
internal object UriHealth {

    fun isReadable(context: Context, uri: Uri): Boolean = runCatching {
        context.contentResolver.openInputStream(uri)?.use { /* opening proves readable */ }
        true
    }.getOrDefault(false)

    fun isReadable(context: Context, uriString: String): Boolean = runCatching {
        isReadable(context, Uri.parse(uriString))
    }.getOrDefault(false)
}
