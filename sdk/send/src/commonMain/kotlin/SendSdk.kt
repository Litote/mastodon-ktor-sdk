package org.litote.mastodon.ktor.sdk.send

import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.configuration.toClientConfiguration
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Response
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2ResponseSuccess
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponse
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client.StatusesApiV1StatusesPostClient.CreateStatusResponseSuccess
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
 * @property visibility Resolved visibility value, or `null` if not set on the status.
 * @property language Language code, or `null` if not set on the status.
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
    ) : SendResult()

    /**
     * The status post request failed after all attachments were uploaded successfully.
     *
     * @property response The raw API response received from the statuses endpoint.
     */
    public data class PostFailure(
        val response: CreateStatusResponse,
    ) : SendResult()

    /**
     * The status was not sent because simulate mode is active.
     *
     * @property info Details about what would have been posted.
     */
    public data class Simulated(
        val info: SimulateInfo,
    ) : SendResult()
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
 */
public class SendSdk public constructor(
    private val clientConfig: ClientConfiguration,
    private val simulate: Boolean = false,
) {
    /** Creates a [SendSdk] configured from the given [SdkConfiguration]. */
    public constructor(config: SdkConfiguration) : this(config.toClientConfiguration(), config.simulate)

    /**
     * Posts a plain-text status.
     *
     * @param text The status to post. [TextStatus.status] must not be blank.
     * @return [SendResult.Success] on success, or [SendResult.PostFailure] if the server rejected the request.
     * @throws IllegalArgumentException if [TextStatus.status] is blank.
     */
    public suspend fun sendText(text: TextStatus): SendResult {
        require(text.status.isNotBlank()) { "Status text is required" }
        if (simulate) {
            return SendResult.Simulated(
                SimulateInfo(
                    text = text.status,
                    visibility = text.visibility?.name?.lowercase(),
                    language = text.language,
                    spoilerText = text.spoilerText,
                    inReplyToId = text.inReplyToId,
                ),
            )
        }
        val client = StatusesApiV1StatusesPostClient(clientConfig)
        return when (val response = client.createStatus(text)) {
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
        if (simulate) {
            return SendResult.Simulated(
                SimulateInfo(
                    text = status.status ?: "",
                    visibility = status.visibility?.name?.lowercase(),
                    language = status.language,
                    spoilerText = status.spoilerText,
                    inReplyToId = status.inReplyToId,
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
        return when (val response = client.createStatus(status.copy(mediaIds = mediaIds))) {
            is CreateStatusResponseSuccess -> {
                SendResult.Success(response.body)
            }

            else -> {
                SendResult.PostFailure(response)
            }
        }
    }
}
