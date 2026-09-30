package org.litote.mastodon.ktor.sdk.send

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    private fun configWith(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): ClientConfiguration {
        val client =
            HttpClient(MockEngine(handler)) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig)
    }

    private fun MockRequestHandleScope.respondJson(
        content: String,
        status: HttpStatusCode,
    ): HttpResponseData = respond(content, status, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun failingConfig(
        failPath: String,
        status: HttpStatusCode,
        body: String,
    ): ClientConfiguration =
        configWith { request ->
            when (request.url.encodedPath) {
                failPath -> respondJson(body, status)
                "/api/v2/media" -> respondJson(mediaJson, HttpStatusCode.OK)
                else -> respondJson(statusJson, HttpStatusCode.OK)
            }
        }

    private fun simulateConfig(): ClientConfiguration = ClientConfiguration(baseUrl = "https://mastodon.social/", json = jsonConfig)

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

    @Test
    fun `GIVEN simulate=true WHEN sendText THEN returns Simulated without calling server`() =
        runTest {
            val config = ClientConfiguration(baseUrl = "https://mastodon.social/", json = jsonConfig)
            val sdk = SendSdk(config, simulate = true)

            val result = sdk.sendText(TextStatus(status = "Hello simulate"))

            assertIs<SendResult.Simulated>(result)
            val info = result.info
            assertEquals("Hello simulate", info.text)
            assertTrue(info.attachments.isEmpty())
        }

    @Test
    fun `GIVEN simulate=true and visibility WHEN sendText THEN SimulateInfo carries visibility`() =
        runTest {
            val config = ClientConfiguration(baseUrl = "https://mastodon.social/", json = jsonConfig)
            val sdk = SendSdk(config, simulate = true)
            val visibility =
                org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum.PUBLIC

            val result = sdk.sendText(TextStatus(status = "Hi", visibility = visibility))

            assertIs<SendResult.Simulated>(result)
            assertEquals("public", result.info.visibility)
        }

    @Test
    fun `GIVEN simulate=true WHEN sendMedia THEN returns Simulated with attachment info`() =
        runTest {
            val config = ClientConfiguration(baseUrl = "https://mastodon.social/", json = jsonConfig)
            val sdk = SendSdk(config, simulate = true)
            val status = MediaStatus(mediaIds = emptyList(), status = "Look!")

            val result = sdk.sendMedia(status, listOf(fakeAttachment()))

            assertIs<SendResult.Simulated>(result)
            val info = result.info
            assertEquals("Look!", info.text)
            assertEquals(1, info.attachments.size)
            assertEquals(3, info.attachments[0].sizeBytes)
        }

    @Test
    fun `GIVEN default visibility and status without visibility WHEN sendText in simulate THEN SimulateInfo uses default`() =
        runTest {
            val sdk = SendSdk(simulateConfig(), simulate = true, defaultVisibility = StatusVisibilityEnum.PRIVATE)

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.Simulated>(result)
            assertEquals("private", result.info.visibility)
        }

    @Test
    fun `GIVEN default language and status without language WHEN sendText in simulate THEN SimulateInfo uses default`() =
        runTest {
            val sdk = SendSdk(simulateConfig(), simulate = true, defaultLanguage = "fr")

            val result = sdk.sendText(TextStatus(status = "Salut"))

            assertIs<SendResult.Simulated>(result)
            assertEquals("fr", result.info.language)
        }

    @Test
    fun `GIVEN status with explicit visibility and language WHEN sendText in simulate THEN explicit values win`() =
        runTest {
            val sdk =
                SendSdk(
                    simulateConfig(),
                    simulate = true,
                    defaultVisibility = StatusVisibilityEnum.PRIVATE,
                    defaultLanguage = "fr",
                )

            val result =
                sdk.sendText(TextStatus(status = "Hi", visibility = StatusVisibilityEnum.PUBLIC, language = "de"))

            assertIs<SendResult.Simulated>(result)
            assertEquals("public", result.info.visibility)
            assertEquals("de", result.info.language)
        }

    @Test
    fun `GIVEN default media visibility and language WHEN sendMedia in simulate THEN SimulateInfo uses defaults`() =
        runTest {
            val sdk =
                SendSdk(
                    simulateConfig(),
                    simulate = true,
                    defaultVisibility = StatusVisibilityEnum.DIRECT,
                    defaultLanguage = "es",
                )

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.Simulated>(result)
            assertEquals("direct", result.info.visibility)
            assertEquals("es", result.info.language)
        }

    @Test
    fun `GIVEN SdkConfiguration visibility and language WHEN sendText in simulate THEN SimulateInfo uses config values`() =
        runTest {
            val sdk =
                SendSdk(
                    SdkConfiguration(
                        server = "mastodon.social",
                        token = "token",
                        visibility = "PRIVATE",
                        language = "fr",
                        simulate = true,
                    ),
                )

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.Simulated>(result)
            assertEquals("private", result.info.visibility)
            assertEquals("fr", result.info.language)
        }

    @Test
    fun `GIVEN SdkConfiguration with unknown visibility WHEN sendText in simulate THEN visibility is left unset`() =
        runTest {
            val sdk =
                SendSdk(
                    SdkConfiguration(server = "mastodon.social", token = "token", visibility = "bogus", simulate = true),
                )

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.Simulated>(result)
            assertNull(result.info.visibility)
        }

    @Test
    fun `GIVEN default visibility and language WHEN sendText and sendMedia THEN request bodies carry defaults`() =
        runTest {
            val statusBodies = mutableListOf<String>()
            val config =
                configWith { request ->
                    if (request.url.encodedPath == "/api/v1/statuses") {
                        statusBodies += request.body.toByteArray().decodeToString()
                        respondJson(statusJson, HttpStatusCode.OK)
                    } else {
                        respondJson(mediaJson, HttpStatusCode.OK)
                    }
                }
            val sdk = SendSdk(config, defaultVisibility = StatusVisibilityEnum.PRIVATE, defaultLanguage = "fr")

            sdk.sendText(TextStatus(status = "Hi"))
            sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertEquals(2, statusBodies.size)
            statusBodies.forEach { body ->
                assertContains(body, "\"visibility\":\"private\"")
                assertContains(body, "\"language\":\"fr\"")
            }
        }

    @Test
    fun `GIVEN server returns 422 with error WHEN sendText THEN errorMessage contains server message`() =
        runTest {
            val sdk =
                SendSdk(
                    failingConfig(
                        "/api/v1/statuses",
                        HttpStatusCode.UnprocessableEntity,
                        """{"error": "Validation failed: Text can't be blank"}""",
                    ),
                )

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.PostFailure>(result)
            assertEquals("Failed to post status: Validation failed: Text can't be blank", result.errorMessage)
        }

    @Test
    fun `GIVEN server returns error with description WHEN sendText THEN errorMessage contains description`() =
        runTest {
            val sdk =
                SendSdk(
                    failingConfig(
                        "/api/v1/statuses",
                        HttpStatusCode.Unauthorized,
                        """{"error": "invalid_token", "error_description": "The access token is invalid"}""",
                    ),
                )

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.PostFailure>(result)
            assertEquals("Failed to post status: invalid_token (The access token is invalid)", result.errorMessage)
        }

    @Test
    fun `GIVEN server returns 500 without body WHEN sendText THEN errorMessage contains HTTP status`() =
        runTest {
            val sdk = SendSdk(failingConfig("/api/v1/statuses", HttpStatusCode.InternalServerError, ""))

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.PostFailure>(result)
            assertEquals("Failed to post status: HTTP 500", result.errorMessage)
        }

    @Test
    fun `GIVEN server returns 410 WHEN sendText THEN errorMessage contains HTTP 410`() =
        runTest {
            val sdk = SendSdk(failingConfig("/api/v1/statuses", HttpStatusCode.Gone, ""))

            val result = sdk.sendText(TextStatus(status = "Hi"))

            assertIs<SendResult.PostFailure>(result)
            assertEquals("Failed to post status: HTTP 410", result.errorMessage)
        }

    @Test
    fun `GIVEN media upload returns 422 with error WHEN sendMedia THEN errorMessage contains server message`() =
        runTest {
            val sdk =
                SendSdk(
                    failingConfig(
                        "/api/v2/media",
                        HttpStatusCode.UnprocessableEntity,
                        """{"error": "File type not supported"}""",
                    ),
                )

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.UploadFailure>(result)
            assertEquals("Failed to upload media: File type not supported", result.errorMessage)
        }

    @Test
    fun `GIVEN media upload returns 410 WHEN sendMedia THEN errorMessage contains HTTP 410`() =
        runTest {
            val sdk = SendSdk(failingConfig("/api/v2/media", HttpStatusCode.Gone, ""))

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.UploadFailure>(result)
            assertEquals("Failed to upload media: HTTP 410", result.errorMessage)
        }

    @Test
    fun `GIVEN media upload returns unexpected status WHEN sendMedia THEN errorMessage contains HTTP status`() =
        runTest {
            val sdk = SendSdk(failingConfig("/api/v2/media", HttpStatusCode.BadGateway, ""))

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.UploadFailure>(result)
            assertEquals("Failed to upload media: HTTP 502", result.errorMessage)
        }
}
