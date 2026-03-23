package org.litote.mastodon.ktor.sdk.cli

import io.ktor.http.ContentType
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile

public class CommandLine {
    public data class MediaAttachment(
        val filePath: String,
        val description: String? = null,
    )

    public fun toForm(attachment: MediaAttachment): CreateMediaV2Form {
        val bytes = SystemFileSystem.source(Path(attachment.filePath)).buffered().readByteArray()
        return CreateMediaV2Form(
            file = CreateMediaV2FormFile(bytes, contentTypeForExtension(attachment.filePath)),
            description = attachment.description,
        )
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
