package org.litote.mastodon.ktor.sdk.send

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.litote.mastodon.ktor.sdk.cli.CommandLine
import org.litote.mastodon.ktor.sdk.cli.parseArgs
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

/**
 * JVM entry point for the `SendMedia` CLI tool.
 *
 * Uploads up to four media attachments and posts a status referencing them to a Mastodon instance.
 *
 * Usage:
 * ```
 * SendMedia --server <host> --token <access_token> [--visibility <v>] [--language <l>] [--simulate]
 *           <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]
 * ```
 *
 * Whether the argument after a file path is an alt-text description or the next file path is
 * determined by probing the filesystem: if the argument resolves to an existing regular file it is
 * treated as the next attachment; otherwise it is treated as a description for the preceding file.
 *
 * Pass `--simulate` to print what would be posted without actually uploading or sending.
 */
public suspend fun main(args: Array<String>): Unit = runSendMedia(args)

internal suspend fun runSendMedia(args: Array<String>) {
    val parsed = parseArgs(args)
    val config = parsed.config
    val remaining = parsed.remaining
    require(remaining.size >= 2) {
        "Usage: SendMedia [options] <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]"
    }

    val commandLine = CommandLine()
    val mediaAttachments =
        buildList {
            var i = 1
            while (i < remaining.size && size < 4) {
                val filePath = remaining[i]
                val nextArg = remaining.getOrNull(i + 1)
                val nextIsFile =
                    nextArg != null &&
                        SystemFileSystem.metadataOrNull(Path(nextArg))?.isRegularFile == true
                val description = if (nextIsFile || nextArg == null) null else nextArg
                add(CommandLine.MediaAttachment(filePath, description))
                i += if (description != null) 2 else 1
            }
        }
    val visibilityEnum =
        StatusVisibilityEnum.entries.firstOrNull { it.name == config.visibility.uppercase() }
            ?: StatusVisibilityEnum.UNLISTED

    val forms = mediaAttachments.map { commandLine.toForm(it) }
    when (
        val result =
            SendSdk(config).sendMedia(
                status =
                    MediaStatus(
                        status = remaining[0],
                        mediaIds = emptyList(),
                        visibility = visibilityEnum,
                        language = config.language,
                    ),
                attachments = forms,
            )
    ) {
        is SendResult.Simulated -> {
            val info = result.info
            println("[simulate] server:     ${config.server}")
            println("[simulate] visibility: ${info.visibility ?: visibilityEnum.name.lowercase()}")
            println("[simulate] language:   ${info.language ?: config.language}")
            println("[simulate] text:       ${info.text}")
            info.attachments.forEachIndexed { index, attachment ->
                val filePath = mediaAttachments.getOrNull(index)?.filePath ?: "(unknown)"
                val alt = attachment.description ?: "(no alt text)"
                println("[simulate] attach[$index]: $filePath — $alt (${attachment.sizeBytes} bytes)")
            }
        }

        is SendResult.Success -> {
            Unit
        }

        is SendResult.PostFailure -> {
            error("Failed to post status: ${result.response}")
        }

        is SendResult.UploadFailure -> {
            error("Failed to upload media: ${result.form}")
        }
    }
}
