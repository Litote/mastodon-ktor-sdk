package org.litote.mastodon.ktor.sdk.send

import org.litote.mastodon.ktor.sdk.cli.parseArgs
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

public suspend fun main(args: Array<String>) {
    val (config, remaining) = parseArgs(args)
    val sdk = SendSdk(config)
    sdk.sendText(
        TextStatus(
            status = remaining.joinToString(" "),
            visibility =
                StatusVisibilityEnum.entries.firstOrNull { it.name == config.visibility.uppercase() }
                    ?: StatusVisibilityEnum.UNLISTED,
            language = config.language,
        ),
    )
}
