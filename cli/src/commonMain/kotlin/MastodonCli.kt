package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands

internal class MastodonCli : SuspendingCliktCommand(name = "mastodon-cli") {
    init {
        subcommands(SendTextCommand(), SendMediaCommand())
    }

    override suspend fun run() = Unit
}
