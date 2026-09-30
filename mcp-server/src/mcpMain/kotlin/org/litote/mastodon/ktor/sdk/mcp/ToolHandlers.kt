package org.litote.mastodon.ktor.sdk.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.model.Search
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.read.DEFAULT_READ_LIMIT
import org.litote.mastodon.ktor.sdk.read.MAX_READ_LIMIT
import org.litote.mastodon.ktor.sdk.read.ReadResult
import org.litote.mastodon.ktor.sdk.read.SearchType
import org.litote.mastodon.ktor.sdk.send.DeleteResult
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.mediaContentType
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsgetE402785c.model.Notification

internal const val ARG_TEXT = "text"
internal const val ARG_VISIBILITY = "visibility"
internal const val ARG_LANGUAGE = "language"
internal const val ARG_SPOILER_TEXT = "spoiler_text"
internal const val ARG_IN_REPLY_TO_ID = "in_reply_to_id"
internal const val ARG_FILES = "files"
internal const val ARG_DESCRIPTIONS = "descriptions"
internal const val ARG_ID = "id"
internal const val ARG_LIMIT = "limit"
internal const val ARG_QUERY = "query"
internal const val ARG_TYPE = "type"

/** Maximum number of media attachments per status. */
internal const val MAX_ATTACHMENTS = 4

internal suspend fun handleSendTextStatus(
    args: Map<String, JsonElement>?,
    sendText: suspend (TextStatus) -> SendResult,
): CallToolResult {
    val text = args.string(ARG_TEXT)
    if (text.isNullOrBlank()) {
        return errorResult("Missing or empty '$ARG_TEXT' argument")
    }
    val status =
        TextStatus(
            status = text,
            visibility = args.visibility(),
            language = args.string(ARG_LANGUAGE),
            spoilerText = args.string(ARG_SPOILER_TEXT),
            inReplyToId = args.string(ARG_IN_REPLY_TO_ID),
        )
    return sendText(status).toToolResult()
}

internal suspend fun handleSendMediaStatus(
    args: Map<String, JsonElement>?,
    sendMedia: suspend (MediaStatus, List<CreateMediaV2Form>) -> SendResult,
): CallToolResult {
    val files = args.stringList(ARG_FILES)
    if (files.isNullOrEmpty() || files.size > MAX_ATTACHMENTS) {
        return errorResult("'$ARG_FILES' must be an array of 1 to $MAX_ATTACHMENTS local file paths")
    }
    val descriptions = args.stringList(ARG_DESCRIPTIONS).orEmpty()
    val forms =
        files.mapIndexed { index, file ->
            val bytes = readLocalFile(file) ?: return errorResult("File not found or not a regular file: $file")
            CreateMediaV2Form(
                file = CreateMediaV2FormFile(bytes, mediaContentType(file), Path(file).name),
                description = descriptions.getOrNull(index)?.takeIf { it.isNotBlank() },
            )
        }
    val status =
        MediaStatus(
            status = args.string(ARG_TEXT)?.takeIf { it.isNotBlank() },
            mediaIds = emptyList(),
            visibility = args.visibility(),
            language = args.string(ARG_LANGUAGE),
            spoilerText = args.string(ARG_SPOILER_TEXT),
            inReplyToId = args.string(ARG_IN_REPLY_TO_ID),
        )
    return sendMedia(status, forms).toToolResult()
}

internal suspend fun handleDeleteStatus(
    args: Map<String, JsonElement>?,
    deleteStatus: suspend (String) -> DeleteResult,
): CallToolResult {
    val id = args.string(ARG_ID)
    if (id.isNullOrBlank()) {
        return errorResult("Missing or empty '$ARG_ID' argument")
    }
    return when (val result = deleteStatus(id)) {
        is DeleteResult.Success -> textResult("Status $id deleted")
        is DeleteResult.Simulated -> textResult("[simulate] delete: ${result.id}")
        is DeleteResult.Failure -> errorResult(result.errorMessage)
    }
}

internal suspend fun handleGetHomeTimeline(
    args: Map<String, JsonElement>?,
    homeTimeline: suspend (Int) -> ReadResult<List<Status>>,
): CallToolResult =
    homeTimeline(args.limit()).toToolResult { statuses ->
        statuses.joinToString("\n\n") { it.toText() }.ifEmpty { "No statuses." }
    }

internal suspend fun handleGetNotifications(
    args: Map<String, JsonElement>?,
    notifications: suspend (Int) -> ReadResult<List<Notification>>,
): CallToolResult =
    notifications(args.limit()).toToolResult { items ->
        items.joinToString("\n\n") { it.toText() }.ifEmpty { "No notifications." }
    }

internal suspend fun handleSearch(
    args: Map<String, JsonElement>?,
    search: suspend (String, SearchType?, Int) -> ReadResult<Search>,
): CallToolResult {
    val query = args.string(ARG_QUERY)
    if (query.isNullOrBlank()) {
        return errorResult("Missing or empty '$ARG_QUERY' argument")
    }
    val typeArg = args.string(ARG_TYPE)
    val type = typeArg?.let { value -> SearchType.entries.firstOrNull { it.name.equals(value, ignoreCase = true) } }
    if (typeArg != null && type == null) {
        return errorResult("Invalid '$ARG_TYPE' argument: expected accounts, statuses or hashtags")
    }
    return search(query, type, args.limit()).toToolResult { it.toText() }
}

private fun SendResult.toToolResult(): CallToolResult =
    when (this) {
        is SendResult.Simulated -> {
            val lines =
                buildList {
                    add("[simulate] text:       ${info.text}")
                    info.visibility?.let { add("[simulate] visibility: $it") }
                    info.language?.let { add("[simulate] language:   $it") }
                    info.spoilerText?.let { add("[simulate] cw:         $it") }
                    info.inReplyToId?.let { add("[simulate] reply_to:   $it") }
                    info.attachments.forEachIndexed { index, attachment ->
                        val alt = attachment.description ?: "(no alt text)"
                        add("[simulate] attach[$index]: ${attachment.contentType} — $alt (${attachment.sizeBytes} bytes)")
                    }
                }
            textResult(lines.joinToString("\n"))
        }

        is SendResult.Success -> {
            val url = (status as? Status)?.let { it.url ?: it.uri } ?: "posted"
            textResult("Status posted: $url")
        }

        is SendResult.PostFailure -> {
            errorResult(errorMessage)
        }

        is SendResult.UploadFailure -> {
            errorResult(errorMessage)
        }

        is SendResult.MediaProcessingFailure -> {
            errorResult(errorMessage)
        }
    }

private fun <T> ReadResult<T>.toToolResult(render: (T) -> String): CallToolResult =
    when (this) {
        is ReadResult.Success -> textResult(render(value))
        is ReadResult.Failure -> errorResult(errorMessage)
    }

private fun textResult(text: String): CallToolResult = CallToolResult(content = listOf(TextContent(text)), isError = false)

private fun errorResult(text: String): CallToolResult = CallToolResult(content = listOf(TextContent(text)), isError = true)

private fun Map<String, JsonElement>?.string(name: String): String? = (this?.get(name) as? JsonPrimitive)?.contentOrNull

private fun Map<String, JsonElement>?.stringList(name: String): List<String>? =
    (this?.get(name) as? JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }

private fun Map<String, JsonElement>?.limit(): Int =
    ((this?.get(ARG_LIMIT) as? JsonPrimitive)?.intOrNull ?: DEFAULT_READ_LIMIT).coerceIn(1, MAX_READ_LIMIT)

private fun Map<String, JsonElement>?.visibility(): StatusVisibilityEnum? =
    string(ARG_VISIBILITY)?.let { value ->
        StatusVisibilityEnum.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }

/** Reads a local file, or returns `null` if [path] does not denote an existing regular file. */
private fun readLocalFile(path: String): ByteArray? {
    val file = Path(path)
    if (SystemFileSystem.metadataOrNull(file)?.isRegularFile != true) return null
    return SystemFileSystem.source(file).buffered().use { it.readByteArray() }
}
