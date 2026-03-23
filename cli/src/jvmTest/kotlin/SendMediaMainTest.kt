package org.litote.mastodon.ktor.sdk.send

import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContains

class SendMediaMainTest {
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

    private fun tempFile(extension: String): String {
        val file = Files.createTempFile("test", ".$extension").toFile()
        file.writeBytes(byteArrayOf(1, 2, 3))
        file.deleteOnExit()
        return file.absolutePath
    }

    @Test
    fun `GIVEN --simulate with attachment WHEN sendMedia main THEN logs config without sending`() =
        runTest {
            val photo = tempFile("jpg")
            val output =
                captureStdout {
                    runSendMedia(
                        arrayOf(
                            "--server",
                            "mastodon.example.com",
                            "--token",
                            "fake-token",
                            "--simulate",
                            "My photo post",
                            photo,
                        ),
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
    fun `GIVEN --simulate with attachment and alt text WHEN sendMedia main THEN logs alt text`() =
        runTest {
            val photo = tempFile("png")
            val output =
                captureStdout {
                    runSendMedia(
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
}
