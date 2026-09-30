package org.litote.mastodon.ktor.sdk.send

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
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
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

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

    private val mediaJson = """{"id": "media123", "type": "image", "url": "https://files.example.com/media123.png"}"""

    private val processingMediaJson = """{"id": "media123", "type": "video", "url": null}"""

    private val processedMediaJson = """{"id": "media123", "type": "video", "url": "https://files.example.com/media123.mp4"}"""

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

    /**
     * Mock configuration whose engine runs on the test scheduler, so that `delay` and timeouts
     * in the polling loop use virtual time.
     */
    private fun TestScope.virtualTimeConfig(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): ClientConfiguration {
        val engineConfig =
            MockEngineConfig().apply {
                dispatcher = StandardTestDispatcher(testScheduler)
                addHandler(handler)
            }
        val client =
            HttpClient(MockEngine(engineConfig)) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig)
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

    @Test
    fun `GIVEN upload returns media without url WHEN sendMedia THEN polls until processed and posts status`() =
        runTest {
            val getCalls = mutableListOf<String>()
            val statusBodies = mutableListOf<String>()
            val config =
                virtualTimeConfig { request ->
                    when (request.url.encodedPath) {
                        "/api/v2/media" -> {
                            respondJson(processingMediaJson, HttpStatusCode.Accepted)
                        }

                        "/api/v1/media/media123" -> {
                            getCalls += request.url.encodedPath
                            if (getCalls.size < 2) {
                                respondJson(processingMediaJson, HttpStatusCode.PartialContent)
                            } else {
                                respondJson(processedMediaJson, HttpStatusCode.OK)
                            }
                        }

                        else -> {
                            statusBodies += request.body.toByteArray().decodeToString()
                            respondJson(statusJson, HttpStatusCode.OK)
                        }
                    }
                }
            val sdk = SendSdk(config, mediaPollInterval = 2.seconds)

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.Success>(result)
            assertEquals(2, getCalls.size)
            assertEquals(4_000, currentTime)
            assertContains(statusBodies.single(), "\"media_ids\":[\"media123\"]")
        }

    @Test
    fun `GIVEN media never finishes processing WHEN sendMedia THEN returns MediaProcessingFailure after timeout`() =
        runTest {
            var statusPosted = false
            val config =
                virtualTimeConfig { request ->
                    when (request.url.encodedPath) {
                        "/api/v2/media" -> {
                            respondJson(processingMediaJson, HttpStatusCode.Accepted)
                        }

                        "/api/v1/media/media123" -> {
                            respondJson(processingMediaJson, HttpStatusCode.PartialContent)
                        }

                        else -> {
                            statusPosted = true
                            respondJson(statusJson, HttpStatusCode.OK)
                        }
                    }
                }
            val sdk = SendSdk(config, mediaPollInterval = 1.seconds, mediaProcessingTimeout = 5.seconds)

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.MediaProcessingFailure>(result)
            assertEquals("media123", result.mediaId)
            assertNull(result.response)
            assertEquals("Media media123 was not processed within the timeout", result.errorMessage)
            assertEquals(5_000, currentTime)
            assertFalse(statusPosted)
        }

    @Test
    fun `GIVEN media processing fails on server WHEN sendMedia THEN returns MediaProcessingFailure with server message`() =
        runTest {
            val config =
                virtualTimeConfig { request ->
                    when (request.url.encodedPath) {
                        "/api/v2/media" -> respondJson(processingMediaJson, HttpStatusCode.Accepted)
                        "/api/v1/media/media123" -> respondJson("""{"error": "Record not found"}""", HttpStatusCode.NotFound)
                        else -> respondJson(statusJson, HttpStatusCode.OK)
                    }
                }
            val sdk = SendSdk(config)

            val result = sdk.sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.MediaProcessingFailure>(result)
            assertEquals("Media media123 processing failed: Record not found", result.errorMessage)
        }

    @Test
    fun `GIVEN media processing returns 410 WHEN sendMedia THEN errorMessage contains HTTP 410`() =
        runTest {
            val config =
                virtualTimeConfig { request ->
                    when (request.url.encodedPath) {
                        "/api/v2/media" -> respondJson(processingMediaJson, HttpStatusCode.Accepted)
                        "/api/v1/media/media123" -> respondJson("", HttpStatusCode.Gone)
                        else -> respondJson(statusJson, HttpStatusCode.OK)
                    }
                }

            val result = SendSdk(config).sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.MediaProcessingFailure>(result)
            assertEquals("Media media123 processing failed: HTTP 410", result.errorMessage)
        }

    @Test
    fun `GIVEN media processing returns unexpected status WHEN sendMedia THEN errorMessage contains HTTP status`() =
        runTest {
            val config =
                virtualTimeConfig { request ->
                    when (request.url.encodedPath) {
                        "/api/v2/media" -> respondJson(processingMediaJson, HttpStatusCode.Accepted)
                        "/api/v1/media/media123" -> respondJson("", HttpStatusCode.BadGateway)
                        else -> respondJson(statusJson, HttpStatusCode.OK)
                    }
                }

            val result = SendSdk(config).sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.MediaProcessingFailure>(result)
            assertEquals("Media media123 processing failed: HTTP 502", result.errorMessage)
        }

    @Test
    fun `GIVEN upload returns media with url WHEN sendMedia THEN does not poll media status`() =
        runTest {
            var getCalled = false
            val config =
                virtualTimeConfig { request ->
                    when (request.url.encodedPath) {
                        "/api/v2/media" -> {
                            respondJson(mediaJson, HttpStatusCode.OK)
                        }

                        "/api/v1/media/media123" -> {
                            getCalled = true
                            respondJson(processedMediaJson, HttpStatusCode.OK)
                        }

                        else -> {
                            respondJson(statusJson, HttpStatusCode.OK)
                        }
                    }
                }

            val result = SendSdk(config).sendMedia(MediaStatus(mediaIds = emptyList()), listOf(fakeAttachment()))

            assertIs<SendResult.Success>(result)
            assertFalse(getCalled)
            assertEquals(0, currentTime)
        }

    @Test
    fun `GIVEN existing status WHEN deleteStatus THEN returns Success and sends DELETE request`() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val sdk =
                SendSdk(
                    configWith { request ->
                        requests += request
                        respondJson(statusJson, HttpStatusCode.OK)
                    },
                )

            val result = sdk.deleteStatus("123456")

            assertIs<DeleteResult.Success>(result)
            assertEquals("123456", result.status.id)
            assertEquals(HttpMethod.Delete, requests.single().method)
            assertEquals("/api/v1/statuses/123456", requests.single().url.encodedPath)
        }

    @Test
    fun `GIVEN simulate=true WHEN deleteStatus THEN returns Simulated without calling server`() =
        runTest {
            val sdk = SendSdk(simulateConfig(), simulate = true)

            val result = sdk.deleteStatus("123456")

            assertEquals(DeleteResult.Simulated("123456"), result)
        }

    @Test
    fun `GIVEN blank id WHEN deleteStatus THEN throws IllegalArgumentException`() =
        runTest {
            assertFailsWith<IllegalArgumentException> { SendSdk(simulateConfig()).deleteStatus(" ") }
        }

    @Test
    fun `GIVEN server errors WHEN deleteStatus THEN errorMessage contains server message or HTTP status`() =
        runTest {
            val cases =
                listOf(
                    Triple(HttpStatusCode.NotFound, """{"error": "Record not found"}""", "Record not found"),
                    Triple(
                        HttpStatusCode.Unauthorized,
                        """{"error": "invalid_token", "error_description": "Token revoked"}""",
                        "invalid_token (Token revoked)",
                    ),
                    Triple(HttpStatusCode.UnprocessableEntity, """{"error": "Validation failed", "details": {}}""", "Validation failed"),
                    Triple(HttpStatusCode.Gone, "", "HTTP 410"),
                    Triple(HttpStatusCode.InternalServerError, "", "HTTP 500"),
                )
            cases.forEach { (status, body, expected) ->
                val sdk = SendSdk(configWith { respondJson(body, status) })

                val result = sdk.deleteStatus("123456")

                assertIs<DeleteResult.Failure>(result)
                assertEquals("Failed to delete status 123456: $expected", result.errorMessage)
            }
        }

    @Test
    fun `GIVEN file names WHEN mediaContentType THEN returns content type from extension`() {
        assertEquals(ContentType.Image.JPEG, mediaContentType("photo.JPG"))
        assertEquals(ContentType.Image.JPEG, mediaContentType("photo.jpeg"))
        assertEquals(ContentType.Image.PNG, mediaContentType("/tmp/a.png"))
        assertEquals(ContentType.Image.GIF, mediaContentType("a.gif"))
        assertEquals(ContentType("image", "webp"), mediaContentType("a.webp"))
        assertEquals(ContentType.Video.MP4, mediaContentType("a.mp4"))
        assertEquals(ContentType("video", "quicktime"), mediaContentType("a.mov"))
        assertEquals(ContentType.Application.OctetStream, mediaContentType("archive.zip"))
        assertEquals(ContentType.Application.OctetStream, mediaContentType("noextension"))
    }
}
