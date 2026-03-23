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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

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
            )
        val visibilityEnum =
            StatusVisibilityEnum.entries.firstOrNull {
                it.name == visibility.get().uppercase()
            } ?: StatusVisibilityEnum.UNLISTED
        val status =
            TextStatus(
                status = text.get(),
                visibility = visibilityEnum,
                language = language.get(),
            )
        if (simulate.get()) {
            logger.lifecycle("[simulate] server:     ${server.get()}")
            logger.lifecycle("[simulate] visibility: ${visibilityEnum.name.lowercase()}")
            logger.lifecycle("[simulate] language:   ${language.get()}")
            logger.lifecycle("[simulate] text:       ${text.get()}")
            return
        }
        when (val result = runBlocking { SendSdk(config).sendText(status) }) {
            is SendResult.Success -> logger.lifecycle("Status posted successfully")
            is SendResult.PostFailure -> error("Failed to post status: ${result.response}")
            is SendResult.UploadFailure -> error("Unexpected upload failure in sendText")
        }
    }
}
