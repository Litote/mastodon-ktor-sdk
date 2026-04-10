package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.CoreSuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands

internal class MastodonCli : CoreSuspendingCliktCommand(name = "mastodon-cli") {
    init {
        subcommands(SendTextCommand(), SendMediaCommand())
    }

    override suspend fun run() = Unit
}
