package com.soulradio.soulradio

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import java.nio.ByteOrder

/** Decoded PCM block fed to [AudioProfiler]. */
data class DecodedPcm(
    val samples: FloatArray,
    val channels: Int,
    val sampleRate: Int,
)

/**
 * MediaCodec-based PCM extractor. Decodes the audio track of [uri] into a
 * single FloatArray for [AudioProfiler]. Reads at most [maxSeconds] of
 * audio; longer files are truncated — the auto-profiler only needs a
 * stable Welch-averaged spectrum, not the whole recording.
 *
 * Synchronous decode loop. Run on a background dispatcher
 * (`Dispatchers.IO`); blocking the main thread on a multi-minute decode
 * would freeze the UI. Caller is also responsible for taking a persistable
 * read permission on the SAF [Uri] before the file leaves the picker
 * scope.
 *
 * No network access; all decoding is local. The radio is offline by
 * design — see CLAUDE.md.
 */
object Decoder {

    private const val DEFAULT_MAX_SECONDS = 30
    private const val DEQUEUE_TIMEOUT_US = 10_000L

    fun decode(context: Context, uri: Uri, maxSeconds: Int = DEFAULT_MAX_SECONDS): DecodedPcm? {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(context, uri, null)
        } catch (_: Throwable) {
            extractor.release()
            return null
        }

        var trackIndex = -1
        var format: MediaFormat? = null
        for (i in 0 until extractor.trackCount) {
            val f = extractor.getTrackFormat(i)
            val mime = f.getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("audio/")) {
                trackIndex = i
                format = f
                break
            }
        }
        if (trackIndex < 0 || format == null) {
            extractor.release()
            return null
        }
        extractor.selectTrack(trackIndex)

        val mime = format.getString(MediaFormat.KEY_MIME)!!
        val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val codec = try {
            MediaCodec.createDecoderByType(mime)
        } catch (_: Throwable) {
            extractor.release()
            return null
        }

        val maxSamples = maxSeconds.toLong().coerceAtMost(60 * 60).toInt() *
            sampleRate * channels
        val out = FloatArray(maxSamples)
        var written = 0

        try {
            codec.configure(format, null, null, 0)
            codec.start()

            val info = MediaCodec.BufferInfo()
            var sawInputEos = false
            var sawOutputEos = false

            while (!sawOutputEos && written < maxSamples) {
                if (!sawInputEos) {
                    val inIdx = codec.dequeueInputBuffer(DEQUEUE_TIMEOUT_US)
                    if (inIdx >= 0) {
                        val inBuf = codec.getInputBuffer(inIdx)!!
                        val read = extractor.readSampleData(inBuf, 0)
                        if (read < 0) {
                            codec.queueInputBuffer(
                                inIdx, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                            )
                            sawInputEos = true
                        } else {
                            codec.queueInputBuffer(inIdx, 0, read, extractor.sampleTime, 0)
                            extractor.advance()
                        }
                    }
                }

                val outIdx = codec.dequeueOutputBuffer(info, DEQUEUE_TIMEOUT_US)
                if (outIdx >= 0) {
                    val buf = codec.getOutputBuffer(outIdx)!!
                    if (info.size > 0) {
                        buf.position(info.offset).limit(info.offset + info.size)
                        val shorts = buf.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                        while (shorts.hasRemaining() && written < maxSamples) {
                            out[written++] = shorts.get() / 32768f
                        }
                    }
                    codec.releaseOutputBuffer(outIdx, false)
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        sawOutputEos = true
                    }
                }
            }
        } catch (_: Throwable) {
            // Whatever we managed to decode is what we return; an error
            // halfway through is still useful if the prefix yielded enough
            // for an FFT frame.
        } finally {
            try { codec.stop() } catch (_: Throwable) { }
            codec.release()
            extractor.release()
        }

        if (written == 0) return null
        return DecodedPcm(
            samples = if (written == out.size) out else out.copyOf(written),
            channels = channels,
            sampleRate = sampleRate,
        )
    }
}
