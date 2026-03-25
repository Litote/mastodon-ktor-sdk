package org.litote.mastodon.ktor.sdk.send

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains

class SendTextMainTest {
    @Test
    fun `GIVEN --simulate WHEN sendText main THEN logs config without sending`(): Unit =
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
    fun `GIVEN --simulate with custom visibility WHEN sendText main THEN logs correct visibility`(): Unit =
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
