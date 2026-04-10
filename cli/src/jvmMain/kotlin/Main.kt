package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.main

public suspend fun main(args: Array<String>): Unit = MastodonCli().main(args)
