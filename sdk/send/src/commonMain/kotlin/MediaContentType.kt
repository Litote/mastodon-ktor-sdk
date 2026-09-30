package org.litote.mastodon.ktor.sdk.send

import io.ktor.http.ContentType

/**
 * Infers the MIME type of a media file from its extension (case-insensitive).
 *
 * Supported extensions: `jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`.
 * Any other extension falls back to `application/octet-stream`.
 *
 * @param fileName Name or path of the file.
 */
public fun mediaContentType(fileName: String): ContentType =
    when (fileName.substringAfterLast('.').lowercase()) {
        "jpg", "jpeg" -> ContentType.Image.JPEG
        "png" -> ContentType.Image.PNG
        "gif" -> ContentType.Image.GIF
        "webp" -> ContentType("image", "webp")
        "mp4" -> ContentType.Video.MP4
        "mov" -> ContentType("video", "quicktime")
        else -> ContentType.Application.OctetStream
    }
