package org.litote.mastodon.ktor.sdk.send

import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.configuration.toClientConfiguration
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Response
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2ResponseFailure
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2ResponseFailure401
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2ResponseSuccess
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2ResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponse
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponseFailure
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponseFailure401
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponseSuccess
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.CreateStatusResponse as StatusBody

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

private fun Error.describe(): String = errorDescription?.let { "$error ($it)" } ?: error

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
 * Create an instance with a [SdkConfiguration] and call [sendText] or [sendMedia].
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
 */
public class SendSdk public constructor(
    private val clientConfig: ClientConfiguration,
    private val simulate: Boolean = false,
    private val defaultVisibility: StatusVisibilityEnum? = null,
    private val defaultLanguage: String? = null,
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
        val client = StatusesApiV1StatusesPostClient(clientConfig)
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
     *
     * @param status The status to post. [MediaStatus.mediaIds] is populated automatically and may be empty.
     * @param attachments Between 1 and 4 (inclusive) multipart forms describing the files to upload.
     * @return [SendResult.Success] on success, [SendResult.UploadFailure] if an attachment could not be
     *   uploaded, or [SendResult.PostFailure] if the server rejected the status request.
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
        val mediaClient = MediaApiV2MediaPostClient(clientConfig)
        val mediaIds = mutableListOf<String>()

        for (form in attachments) {
            when (val response = mediaClient.createMediaV2(form)) {
                is CreateMediaV2ResponseSuccess -> {
                    mediaIds.add(response.body.id)
                }

                else -> {
                    return SendResult.UploadFailure(form, response)
                }
            }
        }

        val client = StatusesApiV1StatusesPostClient(clientConfig)
        return when (val response = client.createStatus(effective.copy(mediaIds = mediaIds))) {
            is CreateStatusResponseSuccess -> {
                SendResult.Success(response.body)
            }

            else -> {
                SendResult.PostFailure(response)
            }
        }
    }
}
