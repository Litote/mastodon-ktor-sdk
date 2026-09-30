package org.litote.mastodon.ktor.sdk.gradle

import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.work.DisableCachingByDefault
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk

/**
 * Gradle task that posts a plain-text status to a Mastodon instance.
 *
 * Typically configured through the `mastodonSend` extension (see [MastodonSendExtension]).
 * The status text must be supplied at task invocation time via the `--text` command-line option
 * or by setting the [text] property in the build script.
 *
 * ```
 * ./gradlew sendText --text "Hello, Mastodon!"
 * ./gradlew sendText --text "Hello" --simulate   # dry-run
 * ```
 */
@DisableCachingByDefault(because = "Posts to an external service; output is not reproducible")
abstract class SendTextTask : DefaultTask() {
    @get:Input
    abstract val server: Property<String>

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val visibility: Property<String>

    @get:Input
    abstract val language: Property<String>

    @get:Input
    @get:Option(option = "text", description = "Status text to post")
    abstract val text: Property<String>

    @get:Input
    @get:Option(option = "simulate", description = "Log what would be posted without actually sending")
    abstract val simulate: Property<Boolean>

    @TaskAction
    fun send() {
        val config =
            SdkConfiguration(
                server = server.get(),
                token = token.get(),
                visibility = visibility.get(),
                language = language.get(),
                simulate = simulate.get(),
            )
        val status =
            TextStatus(
                status = text.get(),
            )
        when (val result = runBlocking { SendSdk(config).sendText(status) }) {
            is SendResult.Simulated -> {
                val info = result.info
                logger.lifecycle("[simulate] server:     ${server.get()}")
                logger.lifecycle("[simulate] visibility: ${info.visibility ?: "(server default)"}")
                logger.lifecycle("[simulate] language:   ${info.language}")
                logger.lifecycle("[simulate] text:       ${info.text}")
            }

            is SendResult.Success -> {
                logger.lifecycle("Status posted successfully")
            }

            is SendResult.PostFailure -> {
                error(result.errorMessage)
            }

            is SendResult.UploadFailure -> {
                error(result.errorMessage)
            }

            is SendResult.MediaProcessingFailure -> {
                error(result.errorMessage)
            }
        }
    }
}
