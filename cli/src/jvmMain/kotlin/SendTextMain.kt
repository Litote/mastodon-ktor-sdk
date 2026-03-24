package org.litote.mastodon.ktor.sdk.send

import org.litote.mastodon.ktor.sdk.cli.parseArgs
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

/**
 * JVM entry point for the `SendText` CLI tool.
 *
 * Posts a plain-text status to a Mastodon instance.
 *
 * Usage:
 * ```
 * SendText --server <host> --token <access_token> [--visibility <v>] [--language <l>] [--simulate] <text…>
 * ```
 *
 * All positional arguments after the named flags are joined with a space and used as the status text.
 * Pass `--simulate` to print what would be posted without actually sending.
 */
public suspend fun main(args: Array<String>): Unit = runSendText(args)

internal suspend fun runSendText(args: Array<String>) {
    val parsed = parseArgs(args)
    val config = parsed.config
    val visibilityEnum =
        StatusVisibilityEnum.entries.firstOrNull { it.name == config.visibility.uppercase() }
            ?: StatusVisibilityEnum.UNLISTED
    val status =
        TextStatus(
            status = parsed.remaining.joinToString(" "),
            visibility = visibilityEnum,
            language = config.language,
        )
    if (parsed.simulate) {
        println("[simulate] server:     ${config.server}")
        println("[simulate] visibility: ${visibilityEnum.name.lowercase()}")
        println("[simulate] language:   ${config.language}")
        println("[simulate] text:       ${status.status}")
        return
    }
    SendSdk(config).sendText(status)
}
