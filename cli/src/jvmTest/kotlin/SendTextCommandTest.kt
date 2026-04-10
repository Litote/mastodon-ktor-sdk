package org.litote.mastodon.ktor.sdk.cli

import com.github.ajalt.clikt.command.parse
import com.github.ajalt.clikt.core.CliktError
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import com.github.ajalt.clikt.command.main as cliktRun

class SendTextCommandTest {
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

    @Test
    fun `GIVEN --simulate WHEN send-text THEN logs config without sending`() =
        runTest {
            val output =
                captureOutput {
                    SendTextCommand().cliktRun(
                        arrayOf("--server", "mastodon.example.com", "--token", "fake-token", "--simulate", "Hello from test!"),
                    )
                }
            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] visibility: unlisted")
            assertContains(output, "[simulate] language:   en")
            assertContains(output, "[simulate] text:       Hello from test!")
        }

    @Test
    fun `GIVEN --simulate with custom visibility WHEN send-text THEN logs correct visibility`() =
        runTest {
            val output =
                captureOutput {
                    SendTextCommand().cliktRun(
                        arrayOf(
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--visibility",
                            "public",
                            "--simulate",
                            "Hello!",
                        ),
                    )
                }
            assertContains(output, "[simulate] visibility: public")
        }

    @Test
    fun `GIVEN --simulate with custom language WHEN send-text THEN logs correct language`() =
        runTest {
            val output =
                captureOutput {
                    SendTextCommand().cliktRun(
                        arrayOf("--server", "mastodon.example.com", "--token", "fake-token", "--language", "fr", "--simulate", "Bonjour!"),
                    )
                }
            assertContains(output, "[simulate] language:   fr")
        }

    @Test
    fun `GIVEN missing --server WHEN send-text THEN throws`() =
        runTest {
            assertFailsWith<CliktError> {
                SendTextCommand().parse(listOf("--token", "fake-token", "Hello!"))
            }
        }

    @Test
    fun `GIVEN missing --token WHEN send-text THEN throws`() =
        runTest {
            assertFailsWith<CliktError> {
                SendTextCommand().parse(listOf("--server", "mastodon.example.com", "Hello!"))
            }
        }
}
