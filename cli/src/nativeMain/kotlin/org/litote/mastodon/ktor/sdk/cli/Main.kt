package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.main
import kotlinx.coroutines.runBlocking

// runBlocking is required here: Kotlin/Native does not support `suspend fun main()`.
public fun main(args: Array<String>): Unit = runBlocking { MastodonCli().main(args) }
