package org.litote.mastodon.ktor.sdk.send

/**
 * Unified JVM entry point for the `mastodon-cli` fat JAR.
 *
 * Dispatches to [runSendText] or [runSendMedia] based on the first positional argument.
 *
 * Usage:
 * ```
 * java -jar mastodon-cli-<version>.jar <command> [options]
 * ```
 *
 * Commands:
 * - `send-text`  — post a plain-text status (see [runSendText])
 * - `send-media` — post a status with media attachments (see [runSendMedia])
 */
public suspend fun main(args: Array<String>): Unit = runMain(args)

internal suspend fun runMain(
    args: Array<String>,
    exitFn: (Int) -> Unit = { exitCode -> System.exit(exitCode) },
) {
    when (args.firstOrNull()) {
        "send-text" -> {
            runSendText(args.drop(1).toTypedArray())
        }

        "send-media" -> {
            runSendMedia(args.drop(1).toTypedArray())
        }

        else -> {
            System.err.println(
                """
                Usage: mastodon-cli <command> [options]

                Commands:
                  send-text   Post a plain-text status
                  send-media  Post a status with media attachments

                Run 'mastodon-cli send-text --help' or 'mastodon-cli send-media --help' for details.
                """.trimIndent(),
            )
            exitFn(1)
        }
    }
}
