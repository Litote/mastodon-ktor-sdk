package org.litote.mastodon.ktor.sdk.gradle

import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.work.DisableCachingByDefault
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import java.io.Serializable

/**
 * Gradle task that uploads media attachments and posts a status referencing them to a Mastodon instance.
 *
 * Typically configured through the `mastodonSend` extension (see [MastodonSendExtension]).
 * Attachments can be added programmatically via [attach] or at the command line with the repeatable
 * `--attach` option (format: `filePath` or `filePath::alt text`).
 *
 * ```kotlin
 * // build.gradle.kts
 * tasks.named<SendMediaTask>("sendMedia") {
 *     text = "Look at this!"
 *     attach("screenshot.png", "A screenshot of the app")
 * }
 * ```
 *
 * ```
 * ./gradlew sendMedia --text "Look!" --attach "screenshot.png::A screenshot"
 * ./gradlew sendMedia --text "Look!" --attach "a.png" --attach "b.png" --simulate
 * ```
 */
@DisableCachingByDefault(because = "Posts to an external service; output is not reproducible")
abstract class SendMediaTask : DefaultTask() {
    /**
     * A media file to be attached to the status.
     *
     * @property filePath Path to the file on the local filesystem.
     * @property description Optional alt-text description for accessibility.
     */
    data class Attachment(
        val filePath: String,
        val description: String? = null,
    ) : Serializable

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
    abstract val attachments: ListProperty<Attachment>

    @get:Input
    @get:Option(
        option = "attach",
        description = "Media attachment as 'filePath' or 'filePath::alt text'. Repeatable up to 4 times.",
    )
    abstract val attachOptions: ListProperty<String>

    @get:Input
    @get:Option(option = "simulate", description = "Log what would be posted without actually sending")
    abstract val simulate: Property<Boolean>

    /**
     * Adds a media attachment to this task from the build script.
     *
     * @param filePath Path to the file on the local filesystem.
     * @param description Optional alt-text description for accessibility.
     */
    fun attach(
        filePath: String,
        description: String? = null,
    ) {
        attachments.add(Attachment(filePath, description))
    }

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
        val allAttachments =
            attachments.get() +
                attachOptions.get().map { option ->
                    val parts = option.split("::", limit = 2)
                    Attachment(filePath = parts[0], description = parts.getOrNull(1))
                }
        val forms =
            allAttachments.map { attachment ->
                val bytes = java.io.File(attachment.filePath).readBytes()
                CreateMediaV2Form(
                    file = CreateMediaV2FormFile(bytes, contentTypeForExtension(attachment.filePath)),
                    description = attachment.description,
                )
            }
        val status =
            MediaStatus(
                status = text.get(),
                mediaIds = emptyList(),
                visibility = visibilityEnum,
                language = language.get(),
            )
        if (simulate.get()) {
            logger.lifecycle("[simulate] server:     ${server.get()}")
            logger.lifecycle("[simulate] visibility: ${visibilityEnum.name.lowercase()}")
            logger.lifecycle("[simulate] language:   ${language.get()}")
            logger.lifecycle("[simulate] text:       ${text.get()}")
            allAttachments.forEachIndexed { index, attachment ->
                val file = java.io.File(attachment.filePath)
                val size = if (file.exists()) "${file.length()} bytes" else "file not found"
                val alt = attachment.description ?: "(no alt text)"
                logger.lifecycle("[simulate] attach[$index]: ${attachment.filePath} — $alt ($size)")
            }
            return
        }
        when (val result = runBlocking { SendSdk(config).sendMedia(status, forms) }) {
            is SendResult.Success -> logger.lifecycle("Status posted successfully")
            is SendResult.PostFailure -> error("Failed to post status: ${result.response}")
            is SendResult.UploadFailure -> error("Failed to upload media: ${result.form}")
        }
    }
}

private fun contentTypeForExtension(path: String): ContentType =
    when (path.substringAfterLast('.').lowercase()) {
        "jpg", "jpeg" -> ContentType.Image.JPEG
        "png" -> ContentType.Image.PNG
        "gif" -> ContentType.Image.GIF
        "webp" -> ContentType("image", "webp")
        "mp4" -> ContentType.Video.MP4
        "mov" -> ContentType("video", "quicktime")
        else -> ContentType.Application.OctetStream
    }
