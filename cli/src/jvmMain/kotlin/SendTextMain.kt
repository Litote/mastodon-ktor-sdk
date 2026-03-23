package org.litote.mastodon.ktor.sdk.send

import org.litote.mastodon.ktor.sdk.cli.parseArgs
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

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
