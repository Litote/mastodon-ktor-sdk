package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

internal class SendTextCommand : SuspendingCliktCommand(name = "send-text") {
    private val server by option("--server", help = "Mastodon instance hostname (e.g. mastodon.social)").required()
    private val token by option("--token", help = "OAuth access token").required()
    private val visibility by option("--visibility", help = "Status visibility: public, unlisted, private, direct").default("unlisted")
    private val language by option("--language", help = "ISO 639-1 language code").default("en")
    private val simulate by option("--simulate", help = "Log what would be posted without actually sending").flag()
    private val text by argument("text", help = "Text of the status to post").multiple(required = true)

    override suspend fun run() {
        val config =
            SdkConfiguration(
                server = server,
                token = token,
                visibility = visibility,
                language = language,
                simulate = simulate,
            )
        val visibilityEnum =
            StatusVisibilityEnum.entries.firstOrNull { it.name == visibility.uppercase() }
                ?: StatusVisibilityEnum.UNLISTED
        val status =
            TextStatus(
                status = text.joinToString(" "),
                visibility = visibilityEnum,
                language = language,
            )
        when (val result = SendSdk(config).sendText(status)) {
            is SendResult.Simulated -> {
                val info = result.info
                echo("[simulate] server:     $server")
                echo("[simulate] visibility: ${info.visibility ?: visibilityEnum.name.lowercase()}")
                echo("[simulate] language:   ${info.language ?: language}")
                echo("[simulate] text:       ${info.text}")
            }

            is SendResult.Success -> {
                Unit
            }

            is SendResult.PostFailure -> {
                throw CliktError("Failed to post status: ${result.response}")
            }

            is SendResult.UploadFailure -> {
                throw CliktError("Unexpected upload failure")
            }
        }
    }
}
