package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.parse
import com.github.ajalt.clikt.core.CliktError
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import com.github.ajalt.clikt.command.main as cliktRun

class SendMediaCommandTest {
    private suspend fun captureOutput(block: suspend () -> Unit): String {
        val out = ByteArrayOutputStream()
        val original = System.out
        System.setOut(PrintStream(out))
        try {
            block()
        } finally {
            System.setOut(original)
        }
        return out.toString()
    }

    private fun tempFile(extension: String): String {
        val file = Files.createTempFile("test", ".$extension").toFile()
        file.writeBytes(byteArrayOf(1, 2, 3))
        file.deleteOnExit()
        return file.absolutePath
    }

    @Test
    fun `GIVEN --simulate with attachment WHEN send-media THEN logs config without sending`() =
        runTest {
            val photo = tempFile("jpg")
            val output =
                captureOutput {
                    SendMediaCommand().cliktRun(
                        arrayOf("--server", "mastodon.example.com", "--token", "fake-token", "--simulate", "My photo post", photo),
                    )
                }
            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] visibility: unlisted")
            assertContains(output, "[simulate] language:   en")
            assertContains(output, "[simulate] text:       My photo post")
            assertContains(output, "[simulate] attach[0]:")
            assertContains(output, "(no alt text)")
        }

    @Test
    fun `GIVEN --simulate with attachment and alt text WHEN send-media THEN logs alt text`() =
        runTest {
            val photo = tempFile("png")
            val output =
                captureOutput {
                    SendMediaCommand().cliktRun(
                        arrayOf(
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--simulate",
                            "My post",
                            photo,
                            "A beautiful sunset",
                        ),
                    )
                }
            assertContains(output, "A beautiful sunset")
            assertContains(output, "3 bytes")
        }

    @Test
    fun `GIVEN no attachment WHEN send-media THEN throws`() =
        runTest {
            assertFailsWith<CliktError> {
                SendMediaCommand().parse(listOf("--server", "mastodon.example.com", "--token", "fake-token", "--simulate", "My post"))
            }
        }
}
