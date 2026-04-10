package org.litote.mastodon.ktor.sdk.mcp

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk
import org.litote.mastodon.ktor.sdk.send.SimulateInfo
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MastodonMcpServerTest {
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
          "url": "https://mastodon.social/@test/123456",
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

    private val statusNoUrlJson =
        statusJson.replace(
            "\"url\": \"https://mastodon.social/@test/123456\",",
            "\"url\": null,",
        )

    private val jsonConfig =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    private fun mockEngineFor(json: String): SendSdk {
        val engine =
            MockEngine { request ->
                when (request.url.encodedPath) {
                    "/api/v1/statuses" -> {
                        respond(
                            content = json,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }

                    else -> {
                        respond(content = """{"error":"not found"}""", status = HttpStatusCode.NotFound)
                    }
                }
            }
        val client =
            HttpClient(engine) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return SendSdk(ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig))
    }

    private fun mockSendSdk(): SendSdk = mockEngineFor(statusJson)

    private fun noUrlSendSdk(): SendSdk = mockEngineFor(statusNoUrlJson)

    private fun failingSendSdk(): SendSdk {
        val engine =
            MockEngine { _ ->
                respond(
                    content = """{"error":"Unauthorized"}""",
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val client =
            HttpClient(engine) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return SendSdk(ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig))
    }

    @Test
    fun `WHEN MastodonMcpServer is constructed THEN no exception is thrown`() {
        MastodonMcpServer(SdkConfiguration(server = "fake.social", token = "fake-token"))
    }

    @Test
    fun `GIVEN valid text WHEN handleSendTextStatus THEN returns success with URL`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("Hello Mastodon!"))

            val result = handleSendTextStatus(args, mockSendSdk()::sendText)

            assertFalse(result.isError ?: false)
            assertTrue(result.content.isNotEmpty())
        }

    @Test
    fun `GIVEN null args WHEN handleSendTextStatus THEN returns isError`() =
        runTest {
            val result = handleSendTextStatus(null, mockSendSdk()::sendText)

            assertTrue(result.isError ?: false)
        }

    @Test
    fun `GIVEN blank text WHEN handleSendTextStatus THEN returns isError`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("   "))

            val result = handleSendTextStatus(args, mockSendSdk()::sendText)

            assertTrue(result.isError ?: false)
        }

    @Test
    fun `GIVEN text with visibility WHEN handleSendTextStatus THEN returns success`() =
        runTest {
            val args =
                mapOf(
                    "text" to JsonPrimitive("Hello!"),
                    "visibility" to JsonPrimitive("public"),
                )

            val result = handleSendTextStatus(args, mockSendSdk()::sendText)

            assertFalse(result.isError ?: false)
        }

    @Test
    fun `GIVEN unrecognized visibility WHEN handleSendTextStatus THEN returns success`() =
        runTest {
            val args =
                mapOf(
                    "text" to JsonPrimitive("Hello!"),
                    "visibility" to JsonPrimitive("invalid_visibility"),
                )

            val result = handleSendTextStatus(args, mockSendSdk()::sendText)

            assertFalse(result.isError ?: false)
        }

    @Test
    fun `GIVEN all optional args WHEN handleSendTextStatus THEN returns success`() =
        runTest {
            val args =
                mapOf(
                    "text" to JsonPrimitive("Hello!"),
                    "language" to JsonPrimitive("fr"),
                    "spoiler_text" to JsonPrimitive("Content warning"),
                    "in_reply_to_id" to JsonPrimitive("12345"),
                )

            val result = handleSendTextStatus(args, mockSendSdk()::sendText)

            assertFalse(result.isError ?: false)
        }

    @Test
    fun `GIVEN post fails WHEN handleSendTextStatus THEN returns isError`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("Hello!"))

            val result = handleSendTextStatus(args, failingSendSdk()::sendText)

            assertTrue(result.isError ?: false)
        }

    @Test
    fun `GIVEN upload fails WHEN handleSendTextStatus THEN returns isError`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("Hello!"))
            val fakeForm =
                MediaApiV2MediaPostClient.CreateMediaV2Form(
                    file =
                        MediaApiV2MediaPostClient.CreateMediaV2FormFile(
                            bytes = ByteArray(0),
                            contentType = ContentType.Image.JPEG,
                        ),
                )

            val result =
                handleSendTextStatus(args) { _ ->
                    SendResult.UploadFailure(fakeForm, MediaApiV2MediaPostClient.CreateMediaV2ResponseFailure)
                }

            assertTrue(result.isError ?: false)
        }

    @Test
    fun `GIVEN success WHEN handleSendTextStatus THEN response contains status URL`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("Hello!"))

            val result = handleSendTextStatus(args, mockSendSdk()::sendText)

            val text =
                result.content
                    .filterIsInstance<TextContent>()
                    .joinToString { it.text }
            assertEquals("Status posted: https://mastodon.social/@test/123456", text)
        }

    @Test
    fun `GIVEN status with null url WHEN handleSendTextStatus THEN response uses uri`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("Hello!"))

            val result = handleSendTextStatus(args, noUrlSendSdk()::sendText)

            val text =
                result.content
                    .filterIsInstance<TextContent>()
                    .joinToString { it.text }
            assertEquals("Status posted: https://mastodon.social/users/test/statuses/123456", text)
        }

    @Test
    fun `GIVEN simulate result WHEN handleSendTextStatus THEN returns simulate info without isError`() =
        runTest {
            val args = mapOf("text" to JsonPrimitive("Hello simulate!"))

            val result =
                handleSendTextStatus(args) { _ ->
                    SendResult.Simulated(
                        SimulateInfo(
                            text = "Hello simulate!",
                            visibility = "public",
                            language = "fr",
                            spoilerText = null,
                            inReplyToId = null,
                        ),
                    )
                }

            assertFalse(result.isError ?: false)
            val text = result.content.filterIsInstance<TextContent>().joinToString { it.text }
            assertContains(text, "[simulate] text:       Hello simulate!")
            assertContains(text, "[simulate] visibility: public")
            assertContains(text, "[simulate] language:   fr")
        }
}
