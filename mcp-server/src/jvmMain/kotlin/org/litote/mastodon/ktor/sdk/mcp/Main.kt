package org.litote.mastodon.ktor.sdk.mcp

import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration

public suspend fun main() {
    val config =
        SdkConfiguration(
            server = System.getenv("MASTODON_SERVER") ?: error("MASTODON_SERVER env var not set"),
            token = System.getenv("MASTODON_TOKEN") ?: error("MASTODON_TOKEN env var not set"),
            visibility = System.getenv("MASTODON_VISIBILITY") ?: "unlisted",
            language = System.getenv("MASTODON_LANGUAGE") ?: "en",
            simulate = System.getenv("MASTODON_SIMULATE")?.equals("true", ignoreCase = true) ?: true,
        )
    MastodonMcpServer(config).start(
        input = System.`in`.asSource().buffered(),
        output = System.out.asSink().buffered(),
    )
}
