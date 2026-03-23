package org.litote.mastodon.ktor.sdk.send

import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertContains

class SendTextMainTest {
    private suspend fun captureStdout(block: suspend () -> Unit): String {
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
    fun `GIVEN --simulate WHEN sendText main THEN logs config without sending`() =
        runTest {
            val output =
                captureStdout {
                    runSendText(
                        arrayOf(
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--simulate",
                            "Hello from test!",
                        ),
                    )
                }

            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] visibility: unlisted")
            assertContains(output, "[simulate] language:   en")
            assertContains(output, "[simulate] text:       Hello from test!")
        }

    @Test
    fun `GIVEN --simulate with custom visibility WHEN sendText main THEN logs correct visibility`() =
        runTest {
            val output =
                captureStdout {
                    runSendText(
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
}
