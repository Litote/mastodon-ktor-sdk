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

class MastodonCliTest {
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
    fun `GIVEN send-text command WHEN main THEN dispatches to send-text`() =
        runTest {
            val output =
                captureOutput {
                    MastodonCli().cliktRun(
                        arrayOf("send-text", "--server", "mastodon.example.com", "--token", "fake-token", "--simulate", "Hello!"),
                    )
                }
            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] text:       Hello!")
        }

    @Test
    fun `GIVEN send-media command WHEN main THEN dispatches to send-media`() =
        runTest {
            val photo = tempFile("jpg")
            val output =
                captureOutput {
                    MastodonCli().cliktRun(
                        arrayOf(
                            "send-media",
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--simulate",
                            "Hello media!",
                            photo,
                        ),
                    )
                }
            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] text:       Hello media!")
        }

    @Test
    fun `GIVEN no arguments WHEN main THEN exits with help`() =
        runTest {
            assertFailsWith<CliktError> {
                MastodonCli().parse(emptyList())
            }
        }

    @Test
    fun `GIVEN unknown command WHEN main THEN exits with error`() =
        runTest {
            assertFailsWith<CliktError> {
                MastodonCli().parse(listOf("unknown-command"))
            }
        }
}
