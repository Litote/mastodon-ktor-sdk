package org.litote.mastodon.ktor.sdk.mcp

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.readByteArray
import platform.posix.STDIN_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix.read

// On Windows, _read()/_write() return Int (32-bit), unlike Unix where ssize_t is 64-bit.
// Each platform has its own actual implementation to avoid the bit-width mismatch in
// the shared serverNativeMain intermediate source set.

@OptIn(ExperimentalForeignApi::class)
internal actual fun nativeStdinSource(): RawSource = WindowsStdinSource()

@OptIn(ExperimentalForeignApi::class)
internal actual fun nativeStdoutSink(): RawSink = WindowsStdoutSink()

@OptIn(ExperimentalForeignApi::class)
private class WindowsStdinSource : RawSource {
    override fun readAtMostTo(
        sink: Buffer,
        byteCount: Long,
    ): Long {
        val capacity = byteCount.coerceAtMost(8192)
        val bytes = ByteArray(capacity.toInt())
        val bytesRead: Int =
            bytes.usePinned { pinned ->
                read(STDIN_FILENO, pinned.addressOf(0), capacity.convert())
            }
        return if (bytesRead <= 0) {
            -1L
        } else {
            sink.write(bytes, 0, bytesRead)
            bytesRead.toLong()
        }
    }

    override fun close() = Unit
}

@OptIn(ExperimentalForeignApi::class)
private class WindowsStdoutSink : RawSink {
    override fun write(
        source: Buffer,
        byteCount: Long,
    ) {
        var remaining = byteCount
        while (remaining > 0L) {
            val chunk = remaining.coerceAtMost(8192)
            val bytes = source.readByteArray(chunk.toInt())
            bytes.usePinned { pinned ->
                platform.posix.write(STDOUT_FILENO, pinned.addressOf(0), chunk.convert())
            }
            remaining -= chunk
        }
    }

    override fun flush() = Unit

    override fun close() = Unit
}
