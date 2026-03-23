package org.litote.mastodon.ktor.sdk.send

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.litote.mastodon.ktor.sdk.cli.CommandLine
import org.litote.mastodon.ktor.sdk.cli.parseArgs
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

// Usage: SendMedia [--server <host>] [--token <token>] [--visibility <v>] [--language <l>]
//                 <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]
//
// Whether the argument after a file path is a description or the next file is determined
// by checking if it exists as a regular file on the filesystem.
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

    if (parsed.simulate) {
        println("[simulate] server:     ${config.server}")
        println("[simulate] visibility: ${visibilityEnum.name.lowercase()}")
        println("[simulate] language:   ${config.language}")
        println("[simulate] text:       ${remaining[0]}")
        mediaAttachments.forEachIndexed { index, attachment ->
            val file = java.io.File(attachment.filePath)
            val size = if (file.exists()) "${file.length()} bytes" else "file not found"
            val alt = attachment.description ?: "(no alt text)"
            println("[simulate] attach[$index]: ${attachment.filePath} — $alt ($size)")
        }
        return
    }

    SendSdk(config).sendMedia(
        status =
            MediaStatus(
                status = remaining[0],
                mediaIds = emptyList(),
                visibility = visibilityEnum,
                language = config.language,
            ),
        attachments = mediaAttachments.map { commandLine.toForm(it) },
    )
}
