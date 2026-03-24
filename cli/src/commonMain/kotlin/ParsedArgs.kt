package org.litote.mastodon.ktor.sdk.cli

import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration

/**
 * Result of parsing CLI arguments.
 *
 * @property config SDK configuration built from the named arguments (`--server`, `--token`, `--visibility`, `--language`).
 * @property remaining Positional arguments left after all named arguments have been consumed.
 * @property simulate When `true`, the command should print what it would do without actually posting.
 */
public data class ParsedArgs(
    val config: SdkConfiguration,
    val remaining: List<String>,
    val simulate: Boolean,
)

/**
 * Parses a CLI argument array into a [ParsedArgs] instance.
 *
 * Named arguments (`--key value`) are extracted and used to build an [SdkConfiguration].
 * The special flag `--simulate` (no value) sets [ParsedArgs.simulate] to `true`.
 * All other arguments are collected into [ParsedArgs.remaining] in order.
 *
 * Required named arguments: `--server`, `--token`.
 * Optional named arguments: `--visibility` (default `unlisted`), `--language` (default `en`).
 *
 * @param args The raw argument array as received by `main`.
 * @return The parsed arguments.
 * @throws IllegalArgumentException if `--server` or `--token` is missing.
 */
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
