package org.litote.mastodon.ktor.sdk.mcp

import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.Job
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status as MastodonStatus

internal class MastodonMcpServer(
    config: SdkConfiguration,
) {
    private val sendSdk = SendSdk(config)

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
                                putJsonObject("text") {
                                    put("type", "string")
                                    put("description", "The text content of the status to post.")
                                }
                                putJsonObject("visibility") {
                                    put("type", "string")
                                    put("description", "Visibility of the status: public, unlisted, private, or direct.")
                                }
                                putJsonObject("language") {
                                    put("type", "string")
                                    put("description", "ISO 639-1 two-letter language code of the status (e.g. 'en', 'fr').")
                                }
                                putJsonObject("spoiler_text") {
                                    put("type", "string")
                                    put("description", "Content warning text shown before the status body.")
                                }
                                putJsonObject("in_reply_to_id") {
                                    put("type", "string")
                                    put("description", "ID of the status this post is replying to.")
                                }
                            },
                        required = listOf("text"),
                    ),
            ) { request ->
                handleSendTextStatus(request.params.arguments) { sendSdk.sendText(it) }
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

internal suspend fun handleSendTextStatus(
    args: Map<String, JsonElement>?,
    sendText: suspend (TextStatus) -> SendResult,
): CallToolResult {
    val text = args?.get("text")?.jsonPrimitive?.content
    if (text.isNullOrBlank()) {
        return CallToolResult(
            content = listOf(TextContent("Missing or empty 'text' argument")),
            isError = true,
        )
    }
    val visibility =
        args["visibility"]?.jsonPrimitive?.content?.let { v ->
            StatusVisibilityEnum.entries.firstOrNull { it.name.lowercase() == v.lowercase() }
        }
    val language = args["language"]?.jsonPrimitive?.content
    val spoilerText = args["spoiler_text"]?.jsonPrimitive?.content
    val inReplyToId = args["in_reply_to_id"]?.jsonPrimitive?.content

    val status =
        TextStatus(
            status = text,
            visibility = visibility,
            language = language,
            spoilerText = spoilerText,
            inReplyToId = inReplyToId,
        )

    return when (val result = sendText(status)) {
        is SendResult.Simulated -> {
            val info = result.info
            val lines =
                buildList {
                    add("[simulate] text:       ${info.text}")
                    info.visibility?.let { add("[simulate] visibility: $it") }
                    info.language?.let { add("[simulate] language:   $it") }
                    info.spoilerText?.let { add("[simulate] cw:         $it") }
                    info.inReplyToId?.let { add("[simulate] reply_to:   $it") }
                }
            CallToolResult(content = listOf(TextContent(lines.joinToString("\n"))), isError = false)
        }

        is SendResult.Success -> {
            val url = (result.status as? MastodonStatus)?.let { it.url ?: it.uri } ?: "posted"
            CallToolResult(content = listOf(TextContent("Status posted: $url")), isError = false)
        }

        is SendResult.PostFailure -> {
            CallToolResult(
                content = listOf(TextContent("Failed to post status")),
                isError = true,
            )
        }

        is SendResult.UploadFailure -> {
            CallToolResult(
                content = listOf(TextContent("Unexpected upload failure")),
                isError = true,
            )
        }
    }
}
