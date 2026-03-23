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

@DisableCachingByDefault(because = "Posts to an external service; output is not reproducible")
abstract class SendMediaTask : DefaultTask() {
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
        val forms =
            attachments.get().map { attachment ->
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
