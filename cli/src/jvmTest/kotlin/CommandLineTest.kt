package org.litote.mastodon.ktor.sdk.cli

import io.ktor.http.ContentType
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandLineTest {
    private val commandLine = CommandLine()

    private fun tempFile(extension: String): String {
        val file = Files.createTempFile("test", ".$extension").toFile()
        file.writeBytes(byteArrayOf(1, 2, 3))
        file.deleteOnExit()
        return file.absolutePath
    }

    @Test
    fun `GIVEN jpg file WHEN toForm THEN content type is image jpeg`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("jpg")))
        assertEquals(ContentType.Image.JPEG, form.file.contentType)
    }

    @Test
    fun `GIVEN jpeg file WHEN toForm THEN content type is image jpeg`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("jpeg")))
        assertEquals(ContentType.Image.JPEG, form.file.contentType)
    }

    @Test
    fun `GIVEN png file WHEN toForm THEN content type is image png`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("png")))
        assertEquals(ContentType.Image.PNG, form.file.contentType)
    }

    @Test
    fun `GIVEN gif file WHEN toForm THEN content type is image gif`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("gif")))
        assertEquals(ContentType.Image.GIF, form.file.contentType)
    }

    @Test
    fun `GIVEN webp file WHEN toForm THEN content type is image webp`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("webp")))
        assertEquals(ContentType("image", "webp"), form.file.contentType)
    }

    @Test
    fun `GIVEN mp4 file WHEN toForm THEN content type is video mp4`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("mp4")))
        assertEquals(ContentType.Video.MP4, form.file.contentType)
    }

    @Test
    fun `GIVEN mov file WHEN toForm THEN content type is video quicktime`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("mov")))
        assertEquals(ContentType("video", "quicktime"), form.file.contentType)
    }

    @Test
    fun `GIVEN unknown extension WHEN toForm THEN content type is octet stream`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("bin")))
        assertEquals(ContentType.Application.OctetStream, form.file.contentType)
    }

    @Test
    fun `GIVEN attachment with description WHEN toForm THEN description is preserved`() {
        val attachment = CommandLine.MediaAttachment(tempFile("png"), description = "A screenshot")
        val form = commandLine.toForm(attachment)
        assertEquals("A screenshot", form.description)
    }

    @Test
    fun `GIVEN attachment without description WHEN toForm THEN description is null`() {
        val form = commandLine.toForm(CommandLine.MediaAttachment(tempFile("png")))
        assertEquals(null, form.description)
    }

    @Test
    fun `GIVEN file WHEN toForm THEN file bytes are read correctly`() {
        val file = Files.createTempFile("test", ".png").toFile()
        val content = byteArrayOf(10, 20, 30, 40)
        file.writeBytes(content)
        file.deleteOnExit()

        val form = commandLine.toForm(CommandLine.MediaAttachment(file.absolutePath))

        assertEquals(content.toList(), form.file.bytes.toList())
    }
}
