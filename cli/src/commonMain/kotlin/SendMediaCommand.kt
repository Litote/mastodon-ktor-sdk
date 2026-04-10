package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

internal class SendMediaCommand : SuspendingCliktCommand(name = "send-media") {
    private val server by option("--server", help = "Mastodon instance hostname (e.g. mastodon.social)").required()
    private val token by option("--token", help = "OAuth access token").required()
    private val visibility by option("--visibility", help = "Status visibility: public, unlisted, private, direct").default("unlisted")
    private val language by option("--language", help = "ISO 639-1 language code").default("en")
    private val simulate by option("--simulate", help = "Log what would be posted without actually sending").flag()
    private val statusText by argument("status", help = "Text of the status to post")
    private val files by argument("files", help = "<file> [description] pairs, up to 4 attachments").multiple(required = true)

    override suspend fun run() {
        val config =
            SdkConfiguration(
                server = server,
                token = token,
                visibility = visibility,
                language = language,
                simulate = simulate,
            )
        val commandLine = CommandLine()
        val mediaAttachments =
            buildList {
                var i = 0
                while (i < files.size && size < 4) {
                    val filePath = files[i]
                    val nextArg = files.getOrNull(i + 1)
                    val nextIsFile =
                        nextArg != null &&
                            SystemFileSystem.metadataOrNull(Path(nextArg))?.isRegularFile == true
                    val description = if (nextIsFile || nextArg == null) null else nextArg
                    add(CommandLine.MediaAttachment(filePath, description))
                    i += if (description != null) 2 else 1
                }
            }
        val visibilityEnum =
            StatusVisibilityEnum.entries.firstOrNull { it.name == visibility.uppercase() }
                ?: StatusVisibilityEnum.UNLISTED
        val forms = mediaAttachments.map { commandLine.toForm(it) }
        when (
            val result =
                SendSdk(config).sendMedia(
                    status =
                        MediaStatus(
                            status = statusText,
                            mediaIds = emptyList(),
                            visibility = visibilityEnum,
                            language = language,
                        ),
                    attachments = forms,
                )
        ) {
            is SendResult.Simulated -> {
                val info = result.info
                echo("[simulate] server:     $server")
                echo("[simulate] visibility: ${info.visibility ?: visibilityEnum.name.lowercase()}")
                echo("[simulate] language:   ${info.language ?: language}")
                echo("[simulate] text:       ${info.text}")
                info.attachments.forEachIndexed { index, attachment ->
                    val filePath = mediaAttachments.getOrNull(index)?.filePath ?: "(unknown)"
                    val alt = attachment.description ?: "(no alt text)"
                    echo("[simulate] attach[$index]: $filePath — $alt (${attachment.sizeBytes} bytes)")
                }
            }

            is SendResult.Success -> {
                Unit
            }

            is SendResult.PostFailure -> {
                throw CliktError("Failed to post status: ${result.response}")
            }

            is SendResult.UploadFailure -> {
                throw CliktError("Failed to upload media: ${result.form}")
            }
        }
    }
}
