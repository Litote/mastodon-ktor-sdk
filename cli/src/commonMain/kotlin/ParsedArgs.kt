package org.litote.mastodon.ktor.sdk.cli

import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration

public data class ParsedArgs(
    val config: SdkConfiguration,
    val remaining: List<String>,
    val simulate: Boolean,
)

public fun parseArgs(args: Array<String>): ParsedArgs {
    var i = 0
    val named = mutableMapOf<String, String>()
    var simulate = false
    val remaining = mutableListOf<String>()
    while (i < args.size) {
        when {
            args[i] == "--simulate" -> {
                simulate = true
                i++
            }

            args[i].startsWith("--") && i + 1 < args.size -> {
                named[args[i]] = args[i + 1]
                i += 2
            }

            else -> {
                remaining.add(args[i])
                i++
            }
        }
    }
    val server = named["--server"]
    val token = named["--token"]
    require(server != null) { "Missing --server <host>" }
    require(token != null) { "Missing --token <access_token>" }
    return ParsedArgs(
        config =
            SdkConfiguration(
                server = server,
                token = token,
                visibility = named["--visibility"] ?: "unlisted",
                language = named["--language"] ?: "en",
            ),
        remaining = remaining,
        simulate = simulate,
    )
}
