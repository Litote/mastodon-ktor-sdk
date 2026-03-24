package org.litote.mastodon.ktor.sdk.send

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MastodonCliMainTest {
    @Test
    fun `GIVEN send-text command WHEN main THEN dispatches to runSendText`(): Unit =
        runTest {
            val output =
                captureStdout {
                    runMain(
                        arrayOf(
                            "send-text",
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--simulate",
                            "Hello!",
                        ),
                    )
                }

            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] text:       Hello!")
        }

    @Test
    fun `GIVEN send-media command WHEN main THEN dispatches to runSendMedia`(): Unit =
        runTest {
            val output =
                captureStdout {
                    runMain(
                        arrayOf(
                            "send-media",
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--simulate",
                            "Hello media!",
                            "/dev/null",
                        ),
                    )
                }

            assertContains(output, "[simulate] server:     mastodon.example.com")
            assertContains(output, "[simulate] text:       Hello media!")
        }

    @Test
    fun `GIVEN unknown command WHEN main THEN prints usage to stderr and calls exit with 1`(): Unit =
        runTest {
            var exitCode: Int? = null
            val stderr =
                captureStderr {
                    runMain(arrayOf("unknown-command")) { code -> exitCode = code }
                }

            assertEquals(1, exitCode)
            assertContains(stderr, "Usage: mastodon-cli <command> [options]")
            assertContains(stderr, "send-text")
            assertContains(stderr, "send-media")
        }

    @Test
    fun `GIVEN no arguments WHEN main THEN prints usage to stderr and calls exit with 1`(): Unit =
        runTest {
            var exitCode: Int? = null
            val stderr =
                captureStderr {
                    runMain(emptyArray()) { code -> exitCode = code }
                }

            assertEquals(1, exitCode)
            assertContains(stderr, "Usage: mastodon-cli <command> [options]")
        }
}
