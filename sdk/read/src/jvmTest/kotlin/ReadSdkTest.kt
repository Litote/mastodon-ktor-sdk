package org.litote.mastodon.ktor.sdk.read

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ReadSdkTest {
    private val accountJson =
        """
        {
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
        """.trimIndent()

    private val statusJson =
        """
        {
          "id": "123456",
          "content": "<p>Hello World</p>",
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
          "visibility": "public",
          "account": $accountJson
        }
        """.trimIndent()

    private val notificationJson =
        """{"id": "n1", "type": "mention", "created_at": "2024-01-01T00:00:00Z", "account": $accountJson, "status": $statusJson}"""

    private val searchJson =
        """
        {
          "accounts": [$accountJson],
          "statuses": [$statusJson],
          "hashtags": [{"name": "kotlin", "url": "https://mastodon.social/tags/kotlin", "history": []}]
        }
        """.trimIndent()

    private val jsonConfig =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    private val requests = mutableListOf<HttpRequestData>()

    private fun sdkWith(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): ReadSdk {
        val client =
            HttpClient(
                MockEngine { request ->
                    requests += request
                    handler(request)
                },
            ) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return ReadSdk(ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig))
    }

    private fun sdkResponding(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): ReadSdk = sdkWith { respond(content, status, headersOf(HttpHeaders.ContentType, "application/json")) }

    @Test
    fun `GIVEN statuses WHEN homeTimeline THEN returns statuses and sends limit`() =
        runTest {
            val result = sdkResponding("[$statusJson]").homeTimeline(limit = 5)

            assertIs<ReadResult.Success<*>>(result)
            val statuses = assertIs<List<*>>(result.value)
            assertEquals(1, statuses.size)
            assertEquals("/api/v1/timelines/home", requests.single().url.encodedPath)
            assertEquals("5", requests.single().url.parameters["limit"])
        }

    @Test
    fun `GIVEN timeline being regenerated WHEN homeTimeline THEN returns Failure`() =
        runTest {
            val result = sdkResponding("", HttpStatusCode.PartialContent).homeTimeline()

            assertEquals(
                ReadResult.Failure("Failed to read home timeline: timeline is being regenerated, try again later"),
                result,
            )
        }

    @Test
    fun `GIVEN notifications WHEN notifications THEN returns notifications and sends limit`() =
        runTest {
            val result = sdkResponding("[$notificationJson]").notifications(limit = 3)

            assertIs<ReadResult.Success<*>>(result)
            assertEquals(1, assertIs<List<*>>(result.value).size)
            assertEquals("/api/v1/notifications", requests.single().url.encodedPath)
            assertEquals("3", requests.single().url.parameters["limit"])
        }

    @Test
    fun `GIVEN search results WHEN search with type THEN returns results and sends query parameters`() =
        runTest {
            val result = sdkResponding(searchJson).search("kotlin", SearchType.HASHTAGS, limit = 10)

            assertIs<ReadResult.Success<*>>(result)
            val parameters = requests.single().url.parameters
            assertEquals("/api/v2/search", requests.single().url.encodedPath)
            assertEquals("kotlin", parameters["q"])
            assertEquals("hashtags", parameters["type"])
            assertEquals("10", parameters["limit"])
        }

    @Test
    fun `GIVEN no type WHEN search THEN type parameter is not sent`() =
        runTest {
            sdkResponding(searchJson).search("kotlin")

            assertEquals(null, requests.single().url.parameters["type"])
        }

    @Test
    fun `GIVEN server errors WHEN reading THEN Failure contains server message or HTTP status`() =
        runTest {
            val cases =
                listOf(
                    Triple(
                        HttpStatusCode.Unauthorized,
                        """{"error": "invalid_token", "error_description": "Token revoked"}""",
                        "invalid_token (Token revoked)",
                    ),
                    Triple(HttpStatusCode.Unauthorized, """{"error": "invalid_token"}""", "invalid_token"),
                    Triple(HttpStatusCode.UnprocessableEntity, """{"error": "Validation failed", "details": {}}""", "Validation failed"),
                    Triple(HttpStatusCode.Gone, "", "HTTP 410"),
                    Triple(HttpStatusCode.InternalServerError, "", "HTTP 500"),
                )
            cases.forEach { (status, body, expected) ->
                val sdk = sdkResponding(body, status)

                assertEquals(ReadResult.Failure("Failed to read home timeline: $expected"), sdk.homeTimeline())
                assertEquals(ReadResult.Failure("Failed to read notifications: $expected"), sdk.notifications())
                assertEquals(ReadResult.Failure("Failed to search: $expected"), sdk.search("kotlin"))
            }
        }

    @Test
    fun `GIVEN out of range limit WHEN reading THEN throws IllegalArgumentException`() =
        runTest {
            val sdk = sdkResponding("[]")

            assertFailsWith<IllegalArgumentException> { sdk.homeTimeline(limit = 0) }
            assertFailsWith<IllegalArgumentException> { sdk.notifications(limit = 41) }
            assertFailsWith<IllegalArgumentException> { sdk.search("kotlin", limit = 41) }
        }

    @Test
    fun `GIVEN blank query WHEN search THEN throws IllegalArgumentException`() =
        runTest {
            assertFailsWith<IllegalArgumentException> { sdkResponding(searchJson).search("  ") }
        }

    @Test
    fun `WHEN ReadSdk is built from SdkConfiguration THEN no exception is thrown`() {
        ReadSdk(SdkConfiguration(server = "mastodon.social", token = "token"))
    }
}
