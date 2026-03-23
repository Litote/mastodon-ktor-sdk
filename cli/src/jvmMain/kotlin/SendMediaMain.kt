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
public suspend fun main(args: Array<String>) {
    val (config, remaining) = parseArgs(args)
    require(remaining.size >= 2) {
        "Usage: SendMedia [options] <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]"
    }

    val commandLine = CommandLine()
    val attachments =
        buildList {
            var i = 1
            while (i < remaining.size && size < 4) {
                val filePath = remaining[i]
                val nextArg = remaining.getOrNull(i + 1)
                val nextIsFile =
                    nextArg != null &&
                        SystemFileSystem.metadataOrNull(Path(nextArg))?.isRegularFile == true
                val description = if (nextIsFile || nextArg == null) null else nextArg
                add(commandLine.toForm(CommandLine.MediaAttachment(filePath, description)))
                i += if (description != null) 2 else 1
            }
        }

    val sdk = SendSdk(config)
    sdk.sendMedia(
        status =
            MediaStatus(
                status = remaining[0],
                mediaIds = emptyList(),
                visibility =
                    StatusVisibilityEnum.entries.firstOrNull { it.name == config.visibility.uppercase() }
                        ?: StatusVisibilityEnum.UNLISTED,
                language = config.language,
            ),
        attachments = attachments,
    )
}
