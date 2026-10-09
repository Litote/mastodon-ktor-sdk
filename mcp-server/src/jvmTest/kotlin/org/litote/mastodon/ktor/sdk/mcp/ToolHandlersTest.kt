package org.litote.mastodon.ktor.sdk.mcp

import io.ktor.http.ContentType
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.DeleteStatusResponseFailure410
import org.litote.mastodon.ktor.sdk.api.model.MediaStatus
import org.litote.mastodon.ktor.sdk.api.model.Notification
import org.litote.mastodon.ktor.sdk.api.model.Search
import org.litote.mastodon.ktor.sdk.api.model.Status
import org.litote.mastodon.ktor.sdk.read.ReadResult
import org.litote.mastodon.ktor.sdk.read.SearchType
import org.litote.mastodon.ktor.sdk.send.AttachmentInfo
import org.litote.mastodon.ktor.sdk.send.DeleteResult
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SimulateInfo
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ToolHandlersTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    private fun accountJson(acct: String) =
        """
        {
          "id": "1", "acct": "$acct", "username": "$acct", "avatar": "a", "avatar_static": "a", "bot": false,
          "created_at": "2024-01-01T00:00:00Z", "display_name": "Display $acct", "emojis": [], "fields": [],
          "followers_count": 0, "following_count": 0, "group": false, "header": "h", "header_static": "h",
          "locked": false, "note": "", "statuses_count": 0, "uri": "https://mastodon.social/users/$acct",
          "url": "https://mastodon.social/@$acct"
        }
        """.trimIndent()

    private fun statusJson(
        id: String = "123",
        acct: String = "alice",
        content: String = "<p>Hello &amp; welcome</p><p>Second line<br/>third</p>",
        spoilerText: String = "",
        url: String? = "https://mastodon.social/@alice/123",
        reblog: String? = null,
    ) = """
        {
          "id": "$id", "content": ${JsonPrimitive(content)}, "created_at": "2024-01-01T00:00:00Z", "emojis": [],
          "favourites_count": 0, "media_attachments": [], "mentions": [], "reblogs_count": 0, "replies_count": 0,
          "sensitive": false, "spoiler_text": "$spoilerText", "tags": [],
          "uri": "https://mastodon.social/users/$acct/statuses/$id", "url": ${url?.let { "\"$it\"" } ?: "null"},
          "visibility": "public", "account": ${accountJson(acct)}, "reblog": ${reblog ?: "null"}
        }
        """.trimIndent()

    private fun status(json: String = statusJson()): Status = this.json.decodeFromString(json)

    private fun CallToolResult.text(): String = content.filterIsInstance<TextContent>().joinToString("\n") { it.text }

    private fun tempFile(
        extension: String,
        size: Int = 3,
    ): String {
        val file = Files.createTempFile("mcp", ".$extension").toFile()
        file.writeBytes(ByteArray(size))
        file.deleteOnExit()
        return file.absolutePath
    }

    // --- send_media_status ---

    @Test
    fun `GIVEN files and descriptions WHEN handleSendMediaStatus THEN sends forms with content type and alt text`() =
        runTest {
            val png = tempFile("png", size = 5)
            val mp4 = tempFile("mp4")
            var sentStatus: MediaStatus? = null
            var sentForms: List<CreateMediaV2Form> = emptyList()
            val args =
                mapOf(
                    "text" to JsonPrimitive("Look!"),
                    "files" to JsonArray(listOf(JsonPrimitive(png), JsonPrimitive(mp4))),
                    "descriptions" to JsonArray(listOf(JsonPrimitive("A picture"))),
                    "visibility" to JsonPrimitive("private"),
                    "language" to JsonPrimitive("fr"),
                    "spoiler_text" to JsonPrimitive("cw"),
                )

            val result =
                handleSendMediaStatus(args) { status, forms ->
                    sentStatus = status
                    sentForms = forms
                    SendResult.Simulated(SimulateInfo("Look!", "private", "fr", "cw", null))
                }

            assertFalse(result.isError ?: false)
            assertEquals("Look!", sentStatus?.status)
            assertEquals("private", sentStatus?.visibility?.serialName())
            assertEquals("fr", sentStatus?.language)
            assertEquals("cw", sentStatus?.spoilerText)
            assertEquals(2, sentForms.size)
            assertEquals(ContentType.Image.PNG, sentForms[0].file.contentType)
            assertEquals(5, sentForms[0].file.bytes.size)
            assertEquals("A picture", sentForms[0].description)
            assertEquals(ContentType.Video.MP4, sentForms[1].file.contentType)
            assertNull(sentForms[1].description)
        }

    @Test
    fun `GIVEN simulate result with attachments WHEN handleSendMediaStatus THEN output lists attachments`() =
        runTest {
            val args = mapOf("files" to JsonArray(listOf(JsonPrimitive(tempFile("jpg")))))

            val result =
                handleSendMediaStatus(args) { _, _ ->
                    SendResult.Simulated(
                        SimulateInfo(
                            text = "",
                            visibility = null,
                            language = null,
                            spoilerText = null,
                            inReplyToId = null,
                            attachments = listOf(AttachmentInfo(3, "image/jpeg", null)),
                        ),
                    )
                }

            assertContains(result.text(), "[simulate] attach[0]: image/jpeg — (no alt text) (3 bytes)")
        }

    @Test
    fun `GIVEN missing or invalid files WHEN handleSendMediaStatus THEN returns isError without sending`() =
        runTest {
            val invalidArgs =
                listOf(
                    null,
                    mapOf("text" to JsonPrimitive("no files")),
                    mapOf("files" to JsonPrimitive("not-an-array")),
                    mapOf("files" to JsonArray(emptyList())),
                    mapOf("files" to JsonArray(List(5) { JsonPrimitive(tempFile("png")) })),
                    mapOf("files" to JsonArray(listOf(JsonPrimitive("/does/not/exist.png")))),
                    mapOf("files" to JsonArray(listOf(JsonPrimitive(Files.createTempDirectory("mcp").toString())))),
                )
            invalidArgs.forEach { args ->
                var sent = false

                val result =
                    handleSendMediaStatus(args) { _, _ ->
                        sent = true
                        SendResult.Simulated(SimulateInfo("", null, null, null, null))
                    }

                assertTrue(result.isError ?: false, "expected error for $args")
                assertFalse(sent)
            }
        }

    @Test
    fun `GIVEN nonexistent file WHEN handleSendMediaStatus THEN error names the file`() =
        runTest {
            val args = mapOf("files" to JsonArray(listOf(JsonPrimitive("/does/not/exist.png"))))

            val result = handleSendMediaStatus(args) { _, _ -> error("must not be called") }

            assertEquals("File not found or not a regular file: /does/not/exist.png", result.text())
        }

    @Test
    fun `GIVEN successful post WHEN handleSendMediaStatus THEN returns status URL`() =
        runTest {
            val args = mapOf("files" to JsonArray(listOf(JsonPrimitive(tempFile("gif")))))

            val result = handleSendMediaStatus(args) { _, _ -> SendResult.Success(json.decodeFromString(statusJson())) }

            assertEquals("Status posted: https://mastodon.social/@alice/123", result.text())
        }

    // --- delete_status ---

    @Test
    fun `GIVEN id WHEN handleDeleteStatus THEN reports deletion`() =
        runTest {
            var deletedId: String? = null

            val result =
                handleDeleteStatus(mapOf("id" to JsonPrimitive("123"))) { id ->
                    deletedId = id
                    DeleteResult.Success(status())
                }

            assertFalse(result.isError ?: false)
            assertEquals("123", deletedId)
            assertEquals("Status 123 deleted", result.text())
        }

    @Test
    fun `GIVEN simulate WHEN handleDeleteStatus THEN reports simulated deletion`() =
        runTest {
            val result = handleDeleteStatus(mapOf("id" to JsonPrimitive("123"))) { DeleteResult.Simulated(it) }

            assertEquals("[simulate] delete: 123", result.text())
        }

    @Test
    fun `GIVEN server failure WHEN handleDeleteStatus THEN returns isError with message`() =
        runTest {
            val result =
                handleDeleteStatus(mapOf("id" to JsonPrimitive("123"))) { DeleteResult.Failure(it, DeleteStatusResponseFailure410()) }

            assertTrue(result.isError ?: false)
            assertEquals("Failed to delete status 123: HTTP 410", result.text())
        }

    @Test
    fun `GIVEN missing id WHEN handleDeleteStatus THEN returns isError`() =
        runTest {
            val result = handleDeleteStatus(mapOf("id" to JsonPrimitive(" "))) { error("must not be called") }

            assertTrue(result.isError ?: false)
        }

    // --- get_home_timeline ---

    @Test
    fun `GIVEN statuses WHEN handleGetHomeTimeline THEN renders plain text statuses`() =
        runTest {
            var requestedLimit = 0
            val boost =
                statusJson(id = "999", acct = "bob", reblog = statusJson(id = "456", acct = "carol", spoilerText = "spoiler", url = null))

            val result =
                handleGetHomeTimeline(mapOf("limit" to JsonPrimitive(5))) { limit ->
                    requestedLimit = limit
                    ReadResult.Success(listOf(status(), status(boost)))
                }

            assertEquals(5, requestedLimit)
            assertEquals(
                """
                @alice · 2024-01-01T00:00:00Z
                Hello & welcome
                Second line
                third
                https://mastodon.social/@alice/123 (id: 123)

                @carol · 2024-01-01T00:00:00Z (boosted by @bob)
                [CW: spoiler] Hello & welcome
                Second line
                third
                https://mastodon.social/users/carol/statuses/456 (id: 456)
                """.trimIndent(),
                result.text(),
            )
        }

    @Test
    fun `GIVEN limit out of range or missing WHEN handleGetHomeTimeline THEN limit is clamped or defaulted`() =
        runTest {
            val limits = mutableListOf<Int>()
            val handler: suspend (Int) -> ReadResult<List<Status>> = { limit ->
                limits += limit
                ReadResult.Success(emptyList())
            }

            handleGetHomeTimeline(mapOf("limit" to JsonPrimitive(500)), handler)
            handleGetHomeTimeline(mapOf("limit" to JsonPrimitive(0)), handler)
            handleGetHomeTimeline(null, handler)
            val empty = handleGetHomeTimeline(mapOf("limit" to JsonPrimitive("abc")), handler)

            assertEquals(listOf(40, 1, 20, 20), limits)
            assertEquals("No statuses.", empty.text())
        }

    @Test
    fun `GIVEN failure WHEN handleGetHomeTimeline THEN returns isError with message`() =
        runTest {
            val result = handleGetHomeTimeline(null) { ReadResult.Failure("Failed to read home timeline: HTTP 500") }

            assertTrue(result.isError ?: false)
            assertEquals("Failed to read home timeline: HTTP 500", result.text())
        }

    // --- get_notifications ---

    @Test
    fun `GIVEN notifications WHEN handleGetNotifications THEN renders type, author and status`() =
        runTest {
            val withStatus =
                json.decodeFromString<Notification>(
                    """{"id": "n1", "type": "mention", "created_at": "2024-01-02T00:00:00Z", "account": ${accountJson(
                        "dave",
                    )}, "status": ${statusJson(content = "<p>Hi @me</p>")}}""",
                )
            val follow =
                json.decodeFromString<Notification>(
                    """{"id": "n2", "type": "follow", "created_at": "2024-01-03T00:00:00Z", "account": ${accountJson("erin")}}""",
                )

            val result = handleGetNotifications(null) { ReadResult.Success(listOf(withStatus, follow)) }

            assertEquals(
                """
                mention from @dave · 2024-01-02T00:00:00Z
                Hi @me
                https://mastodon.social/@alice/123 (id: 123)

                follow from @erin · 2024-01-03T00:00:00Z
                """.trimIndent(),
                result.text(),
            )
        }

    @Test
    fun `GIVEN no notifications or failure WHEN handleGetNotifications THEN renders empty message or error`() =
        runTest {
            assertEquals("No notifications.", handleGetNotifications(null) { ReadResult.Success(emptyList()) }.text())

            val failure = handleGetNotifications(null) { ReadResult.Failure("Failed to read notifications: HTTP 410") }
            assertTrue(failure.isError ?: false)
            assertEquals("Failed to read notifications: HTTP 410", failure.text())
        }

    // --- search ---

    @Test
    fun `GIVEN search results WHEN handleSearch THEN renders accounts, statuses and hashtags`() =
        runTest {
            var request: Triple<String, SearchType?, Int>? = null
            val search =
                json.decodeFromString<Search>(
                    """
                    {
                      "accounts": [${accountJson("frank")}],
                      "statuses": [${statusJson(content = "<p>Kotlin &lt;3</p>")}],
                      "hashtags": [{"name": "kotlin", "url": "https://mastodon.social/tags/kotlin", "history": []}]
                    }
                    """.trimIndent(),
                )
            val args =
                mapOf(
                    "query" to JsonPrimitive("kotlin"),
                    "type" to JsonPrimitive("STATUSES"),
                    "limit" to JsonPrimitive(3),
                )

            val result =
                handleSearch(args) { query, type, limit ->
                    request = Triple(query, type, limit)
                    ReadResult.Success(search)
                }

            assertEquals(Triple("kotlin", SearchType.STATUSES, 3), request)
            assertEquals(
                """
                Accounts:
                - @frank (Display frank) https://mastodon.social/@frank

                Statuses:
                @alice · 2024-01-01T00:00:00Z
                Kotlin <3
                https://mastodon.social/@alice/123 (id: 123)

                Hashtags:
                - #kotlin https://mastodon.social/tags/kotlin
                """.trimIndent(),
                result.text(),
            )
        }

    @Test
    fun `GIVEN empty results WHEN handleSearch without type THEN renders no results`() =
        runTest {
            var requestedType: SearchType? = SearchType.ACCOUNTS

            val result =
                handleSearch(mapOf("query" to JsonPrimitive("nothing"))) { _, type, _ ->
                    requestedType = type
                    ReadResult.Success(Search(accounts = emptyList(), hashtags = emptyList(), statuses = emptyList()))
                }

            assertNull(requestedType)
            assertEquals("No results.", result.text())
        }

    @Test
    fun `GIVEN invalid arguments or failure WHEN handleSearch THEN returns isError`() =
        runTest {
            val handler: suspend (
                String,
                SearchType?,
                Int,
            ) -> ReadResult<Search> = { _, _, _ -> ReadResult.Failure("Failed to search: HTTP 503") }

            assertTrue(handleSearch(null, handler).isError ?: false)
            assertTrue(handleSearch(mapOf("query" to JsonPrimitive("")), handler).isError ?: false)
            val invalidType = handleSearch(mapOf("query" to JsonPrimitive("q"), "type" to JsonPrimitive("toots")), handler)
            assertEquals("Invalid 'type' argument: expected accounts, statuses or hashtags", invalidType.text())
            val failure = handleSearch(mapOf("query" to JsonPrimitive("q")), handler)
            assertTrue(failure.isError ?: false)
            assertEquals("Failed to search: HTTP 503", failure.text())
        }
}
