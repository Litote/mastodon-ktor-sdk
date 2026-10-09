package org.litote.mastodon.ktor.sdk.send

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import org.litote.mastodon.ktor.sdk.api.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.api.client.MediaClient
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2Response
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2ResponseFailure
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2ResponseFailure401
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2ResponseSuccess
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.CreateMediaV2ResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.GetMediaResponse
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.GetMediaResponseFailure
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.GetMediaResponseFailure401
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.GetMediaResponseSuccess
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.GetMediaResponseSuccess200
import org.litote.mastodon.ktor.sdk.api.client.MediaClient.GetMediaResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.CreateStatusResponse
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.CreateStatusResponseFailure
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.CreateStatusResponseFailure401
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.CreateStatusResponseSuccess
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.CreateStatusResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.api.client.StatusesClient.DeleteStatusResponseSuccess
import org.litote.mastodon.ktor.sdk.api.model.Error
import org.litote.mastodon.ktor.sdk.api.model.MediaStatus
import org.litote.mastodon.ktor.sdk.api.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.api.model.TextStatus
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.configuration.toClientConfiguration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import org.litote.mastodon.ktor.sdk.api.model.CreateStatusResponse as StatusBody

/**
 * Describes a media attachment that would be uploaded in simulate mode.
 *
 * @property sizeBytes Size of the attachment in bytes.
 * @property contentType MIME type of the attachment (e.g. `image/png`).
 * @property description Optional alt-text description.
 */
public data class AttachmentInfo(
    val sizeBytes: Int,
    val contentType: String,
    val description: String?,
)

/**
 * Details about the status that would have been posted when simulate mode is active.
 *
 * @property text Status text that would be posted.
 * @property visibility Resolved visibility value (status value, else the SDK default), or `null` if neither is set.
 * @property language Language code (status value, else the SDK default), or `null` if neither is set.
 * @property spoilerText Content warning text, or `null` if not set.
 * @property inReplyToId ID of the status being replied to, or `null` if not set.
 * @property attachments Media attachments that would have been uploaded; empty for text-only statuses.
 */
public data class SimulateInfo(
    val text: String,
    val visibility: String?,
    val language: String?,
    val spoilerText: String?,
    val inReplyToId: String?,
    val attachments: List<AttachmentInfo> = emptyList(),
)

/**
 * Sealed result type returned by [SendSdk] operations.
 *
 * Callers should `when`-exhaustively handle all variants to distinguish success from failure.
 */
public sealed class SendResult {
    /**
     * The status was posted successfully.
     *
     * @property status The Mastodon status object returned by the server.
     */
    public data class Success(
        val status: StatusBody,
    ) : SendResult()

    /**
     * A media attachment could not be uploaded.
     *
     * @property form The multipart form that was submitted when the upload failed.
     * @property response The raw API response received from the media endpoint.
     */
    public data class UploadFailure(
        val form: CreateMediaV2Form,
        val response: CreateMediaV2Response,
    ) : SendResult() {
        /** Human-readable description of the failure, including the server error message when available. */
        public val errorMessage: String
            get() =
                "Failed to upload media: " +
                    when (response) {
                        is CreateMediaV2ResponseFailure401 -> response.body.describe()
                        is CreateMediaV2ResponseFailure -> "HTTP 410"
                        is CreateMediaV2ResponseUnknownFailure -> "HTTP ${response.statusCode}"
                        is CreateMediaV2ResponseSuccess -> "unexpected success response"
                    }
    }

    /**
     * An uploaded media attachment was not processed by the server, so the status was not posted.
     *
     * @property mediaId ID of the media attachment that could not be processed.
     * @property response The last response received from the media endpoint, or `null` if processing
     *   did not finish before the timeout.
     */
    public data class MediaProcessingFailure(
        val mediaId: String,
        val response: GetMediaResponse?,
    ) : SendResult() {
        /** Human-readable description of the failure, including the server error message when available. */
        public val errorMessage: String
            get() =
                when (response) {
                    null -> "Media $mediaId was not processed within the timeout"

                    is GetMediaResponseFailure401 -> "Media $mediaId processing failed: ${response.body.describe()}"

                    is GetMediaResponseFailure -> "Media $mediaId processing failed: HTTP 410"

                    is GetMediaResponseUnknownFailure -> "Media $mediaId processing failed: HTTP ${response.statusCode}"

                    is GetMediaResponseSuccess,
                    is GetMediaResponseSuccess200,
                    -> "Media $mediaId processing failed: unexpected response"
                }
    }

    /**
     * The status post request failed after all attachments were uploaded successfully.
     *
     * @property response The raw API response received from the statuses endpoint.
     */
    public data class PostFailure(
        val response: CreateStatusResponse,
    ) : SendResult() {
        /** Human-readable description of the failure, including the server error message when available. */
        public val errorMessage: String
            get() =
                "Failed to post status: " +
                    when (response) {
                        is CreateStatusResponseFailure401 -> response.body.describe()
                        is CreateStatusResponseFailure -> "HTTP 410"
                        is CreateStatusResponseUnknownFailure -> "HTTP ${response.statusCode}"
                        is CreateStatusResponseSuccess -> "unexpected success response"
                    }
    }

    /**
     * The status was not sent because simulate mode is active.
     *
     * @property info Details about what would have been posted.
     */
    public data class Simulated(
        val info: SimulateInfo,
    ) : SendResult()
}

internal fun Error.describe(): String = errorDescription?.let { "$error ($it)" } ?: error

/**
 * Parses a visibility name (case-insensitive) into a [StatusVisibilityEnum], or `null` if it is not a known value.
 */
internal fun String.toStatusVisibility(): StatusVisibilityEnum? =
    StatusVisibilityEnum.entries.firstOrNull {
        it != StatusVisibilityEnum.UNKNOWN_ && it.name.equals(this, ignoreCase = true)
    }

/**
 * High-level SDK for posting statuses to a Mastodon instance.
 *
 * Create an instance with a [SdkConfiguration] and call [sendText], [sendMedia] or [deleteStatus].
 * Both functions are suspending and must be called from a coroutine context.
 *
 * ```kotlin
 * val sdk = SendSdk(SdkConfiguration(server = "mastodon.social", token = "…"))
 * val result = sdk.sendText(TextStatus(status = "Hello, Mastodon!"))
 * ```
 *
 * @param clientConfig Low-level configuration used by the generated API clients.
 * @param simulate When `true`, no request is sent and [SendResult.Simulated] is returned.
 * @param defaultVisibility Visibility applied to statuses that do not set their own.
 * @param defaultLanguage Language applied to statuses that do not set their own.
 * @param mediaPollInterval Delay between two checks of an uploaded media that is still being processed.
 * @param mediaProcessingTimeout Maximum time to wait for an uploaded media to be processed.
 */
public class SendSdk public constructor(
    private val clientConfig: ClientConfiguration,
    private val simulate: Boolean = false,
    private val defaultVisibility: StatusVisibilityEnum? = null,
    private val defaultLanguage: String? = null,
    private val mediaPollInterval: Duration = 1.seconds,
    private val mediaProcessingTimeout: Duration = 60.seconds,
) {
    /**
     * Creates a [SendSdk] configured from the given [SdkConfiguration].
     *
     * [SdkConfiguration.visibility] and [SdkConfiguration.language] are applied to every status that does not
     * set its own value. An unknown visibility is ignored, letting the server apply the account default.
     */
    public constructor(config: SdkConfiguration) : this(
        clientConfig = config.toClientConfiguration(),
        simulate = config.simulate,
        defaultVisibility = config.visibility.toStatusVisibility(),
        defaultLanguage = config.language,
    )

    /**
     * Posts a plain-text status.
     *
     * @param text The status to post. [TextStatus.status] must not be blank.
     * @return [SendResult.Success] on success, or [SendResult.PostFailure] if the server rejected the request.
     * @throws IllegalArgumentException if [TextStatus.status] is blank.
     */
    public suspend fun sendText(text: TextStatus): SendResult {
        require(text.status.isNotBlank()) { "Status text is required" }
        val effective =
            text.copy(
                visibility = text.visibility ?: defaultVisibility,
                language = text.language ?: defaultLanguage,
            )
        if (simulate) {
            return SendResult.Simulated(
                SimulateInfo(
                    text = effective.status,
                    visibility = effective.visibility?.name?.lowercase(),
                    language = effective.language,
                    spoilerText = effective.spoilerText,
                    inReplyToId = effective.inReplyToId,
                ),
            )
        }
        val client = StatusesClient(clientConfig)
        return when (val response = client.createStatus(effective)) {
            is CreateStatusResponseSuccess -> {
                SendResult.Success(response.body)
            }

            else -> {
                SendResult.PostFailure(response)
            }
        }
    }

    /**
     * Uploads one to four media attachments and then posts a status referencing them.
     *
     * Attachments are uploaded sequentially; the first upload failure immediately returns
     * [SendResult.UploadFailure] without uploading or posting the remaining items.
     * When the server accepts an attachment for asynchronous processing (no `url` yet), the SDK polls
     * `GET /api/v1/media/{id}` every `mediaPollInterval` until it is ready; if it fails or is not ready within
     * `mediaProcessingTimeout`, [SendResult.MediaProcessingFailure] is returned and the status is not posted.
     *
     * @param status The status to post. [MediaStatus.mediaIds] is populated automatically and may be empty.
     * @param attachments Between 1 and 4 (inclusive) multipart forms describing the files to upload.
     * @return [SendResult.Success] on success, [SendResult.UploadFailure] if an attachment could not be
     *   uploaded, [SendResult.MediaProcessingFailure] if an attachment could not be processed, or
     *   [SendResult.PostFailure] if the server rejected the status request.
     * @throws IllegalArgumentException if [attachments] is empty or contains more than 4 items.
     */
    public suspend fun sendMedia(
        status: MediaStatus,
        attachments: List<CreateMediaV2Form>,
    ): SendResult {
        require(attachments.isNotEmpty()) { "At least one attachment is required" }
        require(attachments.size <= 4) { "At most 4 attachments are supported" }
        val effective =
            status.copy(
                visibility = status.visibility ?: defaultVisibility,
                language = status.language ?: defaultLanguage,
            )
        if (simulate) {
            return SendResult.Simulated(
                SimulateInfo(
                    text = effective.status ?: "",
                    visibility = effective.visibility?.name?.lowercase(),
                    language = effective.language,
                    spoilerText = effective.spoilerText,
                    inReplyToId = effective.inReplyToId,
                    attachments =
                        attachments.map { form ->
                            AttachmentInfo(
                                sizeBytes = form.file.bytes.size,
                                contentType = form.file.contentType.toString(),
                                description = form.description,
                            )
                        },
                ),
            )
        }
        val mediaClient = MediaClient(clientConfig)
        val mediaIds = mutableListOf<String>()

        for (form in attachments) {
            when (val response = mediaClient.createMediaV2(form)) {
                is CreateMediaV2ResponseSuccess -> {
                    val media = response.body
                    if (media.url == null) {
                        awaitMediaProcessed(media.id)?.let { return it }
                    }
                    mediaIds.add(media.id)
                }

                else -> {
                    return SendResult.UploadFailure(form, response)
                }
            }
        }

        val client = StatusesClient(clientConfig)
        return when (val response = client.createStatus(effective.copy(mediaIds = mediaIds))) {
            is CreateStatusResponseSuccess -> {
                SendResult.Success(response.body)
            }

            else -> {
                SendResult.PostFailure(response)
            }
        }
    }

    /**
     * Deletes a status authored by the authenticated user.
     *
     * @param id ID of the status to delete. Must not be blank.
     * @return [DeleteResult.Success] on success, [DeleteResult.Failure] if the server refused the request,
     *   or [DeleteResult.Simulated] in simulate mode.
     * @throws IllegalArgumentException if [id] is blank.
     */
    public suspend fun deleteStatus(id: String): DeleteResult {
        require(id.isNotBlank()) { "Status id is required" }
        if (simulate) {
            return DeleteResult.Simulated(id)
        }
        return when (val response = StatusesClient(clientConfig).deleteStatus(id)) {
            is DeleteStatusResponseSuccess -> DeleteResult.Success(response.body)
            else -> DeleteResult.Failure(id, response)
        }
    }

    /**
     * Polls the media endpoint until [mediaId] is processed.
     *
     * @return `null` once the media is ready, or a [SendResult.MediaProcessingFailure] otherwise.
     */
    private suspend fun awaitMediaProcessed(mediaId: String): SendResult.MediaProcessingFailure? {
        val client = MediaClient(clientConfig)
        val response = withTimeoutOrNull(mediaProcessingTimeout) { pollUntilProcessingEnds(client, mediaId) }
        return if (response is GetMediaResponseSuccess200) null else SendResult.MediaProcessingFailure(mediaId, response)
    }

    /** Returns the first response that is not `206 Partial Content` (i.e. processing is no longer in progress). */
    private suspend fun pollUntilProcessingEnds(
        client: MediaClient,
        mediaId: String,
    ): GetMediaResponse {
        while (true) {
            delay(mediaPollInterval)
            val response = client.getMedia(mediaId)
            if (response !is GetMediaResponseSuccess) return response
        }
    }
}
