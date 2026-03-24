package org.litote.mastodon.ktor.sdk.cli

import io.ktor.http.ContentType
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile

/**
 * Utility for CLI-specific operations such as reading media files from the filesystem
 * and converting them to API multipart forms.
 */
public class CommandLine {
    /**
     * A media file to be attached to a status.
     *
     * @property filePath Absolute or relative path to the file on the local filesystem.
     * @property description Optional alt-text description for accessibility.
     */
    public data class MediaAttachment(
        val filePath: String,
        val description: String? = null,
    )

    /**
     * Reads [attachment] from the filesystem and converts it to a [CreateMediaV2Form] ready to be
     * uploaded via the Mastodon media API.
     *
     * The MIME content type is inferred from the file extension (jpg, png, gif, webp, mp4, mov).
     * Unknown extensions fall back to `application/octet-stream`.
     *
     * @param attachment The media attachment descriptor.
     * @return A multipart form containing the file bytes and optional alt-text.
     */
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
