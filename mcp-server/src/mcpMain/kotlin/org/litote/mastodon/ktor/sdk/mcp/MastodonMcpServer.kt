package org.litote.mastodon.ktor.sdk.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.Job
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.read.DEFAULT_READ_LIMIT
import org.litote.mastodon.ktor.sdk.read.MAX_READ_LIMIT
import org.litote.mastodon.ktor.sdk.read.ReadSdk
import org.litote.mastodon.ktor.sdk.send.SendSdk

internal class MastodonMcpServer(
    config: SdkConfiguration,
) {
    private val sendSdk = SendSdk(config)
    private val readSdk = ReadSdk(config)

    private val server =
        Server(
            serverInfo = Implementation(name = "mastodon-mcp-server", version = "1.0"),
            options =
                ServerOptions(
                    capabilities =
                        ServerCapabilities(
                            tools = ServerCapabilities.Tools(listChanged = false),
                        ),
                ),
        ) {
            addTool(
                name = "send_text_status",
                description = "Post a plain text status to Mastodon. Returns the URL of the posted status.",
                inputSchema =
                    ToolSchema(
                        properties =
                            buildJsonObject {
                                stringProperty(ARG_TEXT, "The text content of the status to post.")
                                statusOptions()
                            },
                        required = listOf(ARG_TEXT),
                    ),
            ) { request ->
                handleSendTextStatus(request.params.arguments) { sendSdk.sendText(it) }
            }
            addTool(
                name = "send_media_status",
                description =
                    "Post a status with 1 to $MAX_ATTACHMENTS media attachments read from local files " +
                        "(jpg, png, gif, webp, mp4, mov). Waits for media processing. Returns the URL of the posted status.",
                inputSchema =
                    ToolSchema(
                        properties =
                            buildJsonObject {
                                putJsonObject(ARG_FILES) {
                                    put("type", "array")
                                    putJsonObject("items") { put("type", "string") }
                                    put("minItems", 1)
                                    put("maxItems", MAX_ATTACHMENTS)
                                    put("description", "Absolute paths of the local files to attach.")
                                }
                                putJsonObject(ARG_DESCRIPTIONS) {
                                    put("type", "array")
                                    putJsonObject("items") { put("type", "string") }
                                    put("description", "Alt text of each attachment, in the same order as 'files'.")
                                }
                                stringProperty(ARG_TEXT, "Optional text content of the status.")
                                statusOptions()
                            },
                        required = listOf(ARG_FILES),
                    ),
            ) { request ->
                handleSendMediaStatus(request.params.arguments) { status, forms -> sendSdk.sendMedia(status, forms) }
            }
            addTool(
                name = "delete_status",
                description = "Delete a status posted by the authenticated user.",
                inputSchema =
                    ToolSchema(
                        properties = buildJsonObject { stringProperty(ARG_ID, "ID of the status to delete.") },
                        required = listOf(ARG_ID),
                    ),
            ) { request ->
                handleDeleteStatus(request.params.arguments) { sendSdk.deleteStatus(it) }
            }
            addTool(
                name = "get_home_timeline",
                description = "Read the most recent statuses of the authenticated user's home timeline.",
                inputSchema = ToolSchema(properties = buildJsonObject { limitProperty() }),
            ) { request ->
                handleGetHomeTimeline(request.params.arguments) { readSdk.homeTimeline(it) }
            }
            addTool(
                name = "get_notifications",
                description = "Read the most recent notifications of the authenticated user.",
                inputSchema = ToolSchema(properties = buildJsonObject { limitProperty() }),
            ) { request ->
                handleGetNotifications(request.params.arguments) { readSdk.notifications(it) }
            }
            addTool(
                name = "search",
                description = "Search Mastodon accounts, statuses and hashtags.",
                inputSchema =
                    ToolSchema(
                        properties =
                            buildJsonObject {
                                stringProperty(ARG_QUERY, "Text to search for.")
                                stringProperty(ARG_TYPE, "Restrict results to one kind: accounts, statuses, or hashtags.")
                                limitProperty()
                            },
                        required = listOf(ARG_QUERY),
                    ),
            ) { request ->
                handleSearch(request.params.arguments) { query, type, limit -> readSdk.search(query, type, limit) }
            }
        }

    internal suspend fun start(
        input: Source,
        output: Sink,
    ) {
        val transport = StdioServerTransport(inputStream = input, outputStream = output)
        server.createSession(transport)
        val done = Job()
        server.onClose { done.complete() }
        done.join()
    }
}

private fun JsonObjectBuilder.stringProperty(
    name: String,
    description: String,
) {
    putJsonObject(name) {
        put("type", "string")
        put("description", description)
    }
}

private fun JsonObjectBuilder.limitProperty() {
    putJsonObject(ARG_LIMIT) {
        put("type", "integer")
        put("minimum", 1)
        put("maximum", MAX_READ_LIMIT)
        put("description", "Maximum number of items to return (default $DEFAULT_READ_LIMIT).")
    }
}

private fun JsonObjectBuilder.statusOptions() {
    stringProperty(ARG_VISIBILITY, "Visibility of the status: public, unlisted, private, or direct.")
    stringProperty(ARG_LANGUAGE, "ISO 639-1 two-letter language code of the status (e.g. 'en', 'fr').")
    stringProperty(ARG_SPOILER_TEXT, "Content warning text shown before the status body.")
    stringProperty(ARG_IN_REPLY_TO_ID, "ID of the status this post is replying to.")
}
