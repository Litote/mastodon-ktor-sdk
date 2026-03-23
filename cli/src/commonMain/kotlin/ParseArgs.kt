package org.litote.mastodon.ktor.sdk.cli

import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration

public fun parseArgs(args: Array<String>): Pair<SdkConfiguration, List<String>> {
    val named =
        args.indices
            .filter { args[it].startsWith("--") }
            .associate { args[it] to args[it + 1] }
    val remaining =
        args.filterIndexed { i, arg ->
            !arg.startsWith("--") && (i == 0 || !args[i - 1].startsWith("--"))
        }
    val server = named["--server"]
    val token = named["--token"]
    require(server != null) { "Missing --server <host>" }
    require(token != null) { "Missing --token <access_token>" }
    return SdkConfiguration(
        server = server,
        token = token,
        visibility = named["--visibility"] ?: "unlisted",
        language = named["--language"] ?: "en",
    ) to remaining
}
