package org.litote.mastodon.ktor.sdk.send

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.model.TextStatus
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class SendSdkTest {
    private val statusJson =
        """
        {
          "id": "123456",
          "content": "Hello World",
          "created_at": "2024-01-01T00:00:00Z",
          "emojis": [],
          "favourites_count": 0,
          "media_attachments": [],
          "mentions": [],
          "reblogs_count": 0,
          "replies_count": 0,
          "sensitive": false,
          "spoiler_text": "",
          "tags": [],
          "uri": "https://mastodon.social/users/test/statuses/123456",
          "visibility": "unlisted",
          "account": {
            "id": "1",
            "acct": "test",
            "username": "test",
            "avatar": "https://example.com/avatar.jpg",
            "avatar_static": "https://example.com/avatar.jpg",
            "bot": false,
            "created_at": "2024-01-01T00:00:00Z",
            "display_name": "Test User",
            "emojis": [],
            "fields": [],
            "followers_count": 0,
            "following_count": 0,
            "group": false,
            "header": "https://example.com/header.jpg",
            "header_static": "https://example.com/header.jpg",
            "locked": false,
            "note": "Test bio",
            "statuses_count": 0,
            "uri": "https://mastodon.social/users/test"
          }
        }
        """.trimIndent()

    private val mediaJson = """{"id": "media123", "type": "image"}"""

    private val jsonConfig =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    private fun mockClientConfig(): ClientConfiguration {
        val engine =
            MockEngine { request ->
                when (request.url.encodedPath) {
                    "/api/v1/statuses" -> {
                        respond(
                            content = statusJson,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }

                    "/api/v2/media" -> {
                        respond(
                            content = mediaJson,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }

                    else -> {
                        respondBadRequest()
                    }
                }
            }
        val client =
            HttpClient(engine) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig)
    }

    private fun mockFailingClientConfig(
        failPath: String,
        statusCode: HttpStatusCode,
    ): ClientConfiguration {
        val engine =
            MockEngine { request ->
                when (request.url.encodedPath) {
                    failPath -> {
                        respond(
                            content = """{"error": "Unauthorized"}""",
                            status = statusCode,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }

                    "/api/v1/statuses" -> {
                        respond(
                            content = statusJson,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }

                    "/api/v2/media" -> {
                        respond(
                            content = mediaJson,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }

                    else -> {
                        respondBadRequest()
                    }
                }
            }
        val client =
            HttpClient(engine) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig)
    }

    private fun fakeAttachment() =
        CreateMediaV2Form(
            file = CreateMediaV2FormFile(bytes = byteArrayOf(1, 2, 3), contentType = ContentType.Image.PNG),
        )

    @Test
    fun `GIVEN valid text status WHEN sendText THEN returns Success`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())

            val result = sdk.sendText(TextStatus(status = "Hello World"))

            assertIs<SendResult.Success>(result)
        }

    @Test
    fun `GIVEN successful response WHEN sendText THEN Success contains status id`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())

            val result = sdk.sendText(TextStatus(status = "Hello World"))

            assertIs<SendResult.Success>(result)
        }

    @Test
    fun `GIVEN blank status text WHEN sendText THEN throws IllegalArgumentException`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())

            assertFailsWith<IllegalArgumentException> {
                sdk.sendText(TextStatus(status = "   "))
            }
        }

    @Test
    fun `GIVEN empty status text WHEN sendText THEN throws IllegalArgumentException`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())

            assertFailsWith<IllegalArgumentException> {
                sdk.sendText(TextStatus(status = ""))
            }
        }

    @Test
    fun `GIVEN server returns 401 WHEN sendText THEN returns PostFailure`() =
        runTest {
            val sdk = SendSdk(mockFailingClientConfig("/api/v1/statuses", HttpStatusCode.Unauthorized))

            val result = sdk.sendText(TextStatus(status = "Hello"))

            assertIs<SendResult.PostFailure>(result)
        }

    @Test
    fun `GIVEN one attachment WHEN sendMedia THEN returns Success`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())
            val status = MediaStatus(mediaIds = emptyList())

            val result = sdk.sendMedia(status, listOf(fakeAttachment()))

            assertIs<SendResult.Success>(result)
        }

    @Test
    fun `GIVEN four attachments WHEN sendMedia THEN returns Success`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())
            val status = MediaStatus(mediaIds = emptyList())

            val result = sdk.sendMedia(status, List(4) { fakeAttachment() })

            assertIs<SendResult.Success>(result)
        }

    @Test
    fun `GIVEN empty attachments WHEN sendMedia THEN throws IllegalArgumentException`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())
            val status = MediaStatus(mediaIds = emptyList())

            assertFailsWith<IllegalArgumentException> {
                sdk.sendMedia(status, emptyList())
            }
        }

    @Test
    fun `GIVEN five attachments WHEN sendMedia THEN throws IllegalArgumentException`() =
        runTest {
            val sdk = SendSdk(mockClientConfig())
            val status = MediaStatus(mediaIds = emptyList())

            assertFailsWith<IllegalArgumentException> {
                sdk.sendMedia(status, List(5) { fakeAttachment() })
            }
        }

    @Test
    fun `GIVEN media upload fails WHEN sendMedia THEN returns UploadFailure`() =
        runTest {
            val sdk = SendSdk(mockFailingClientConfig("/api/v2/media", HttpStatusCode.Unauthorized))
            val status = MediaStatus(mediaIds = emptyList())

            val result = sdk.sendMedia(status, listOf(fakeAttachment()))

            assertIs<SendResult.UploadFailure>(result)
        }

    @Test
    fun `GIVEN upload succeeds but post fails WHEN sendMedia THEN returns PostFailure`() =
        runTest {
            val sdk = SendSdk(mockFailingClientConfig("/api/v1/statuses", HttpStatusCode.Unauthorized))
            val status = MediaStatus(mediaIds = emptyList())

            val result = sdk.sendMedia(status, listOf(fakeAttachment()))

            assertIs<SendResult.PostFailure>(result)
        }
}
