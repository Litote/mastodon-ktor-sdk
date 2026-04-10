package org.litote.mastodon.ktor.sdk.mcp

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.runBlocking
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.buffered
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import platform.posix.getenv

internal expect fun nativeStdinSource(): RawSource

internal expect fun nativeStdoutSink(): RawSink

// runBlocking is required here: Kotlin/Native does not support `suspend fun main()`.
@OptIn(ExperimentalForeignApi::class)
public fun main(): Unit =
    runBlocking {
        val config =
            SdkConfiguration(
                server = getenv("MASTODON_SERVER")?.toKString() ?: error("MASTODON_SERVER env var not set"),
                token = getenv("MASTODON_TOKEN")?.toKString() ?: error("MASTODON_TOKEN env var not set"),
                visibility = getenv("MASTODON_VISIBILITY")?.toKString() ?: "unlisted",
                language = getenv("MASTODON_LANGUAGE")?.toKString() ?: "en",
                simulate = getenv("MASTODON_SIMULATE")?.toKString()?.equals("true", ignoreCase = true) ?: true,
            )
        MastodonMcpServer(config).start(
            input = nativeStdinSource().buffered(),
            output = nativeStdoutSink().buffered(),
        )
    }
