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

public sealed class SendResult {
    public data class Success(
        val status: StatusBody,
    ) : SendResult()

    public data class UploadFailure(
        val form: CreateMediaV2Form,
        val response: CreateMediaV2Response,
    ) : SendResult()

    public data class PostFailure(
        val response: CreateStatusResponse,
    ) : SendResult()
}

public class SendSdk internal constructor(
    private val clientConfig: ClientConfiguration,
) {
    public constructor(config: SdkConfiguration) : this(config.toClientConfiguration())

    public suspend fun sendText(text: TextStatus): SendResult {
        require(text.status.isNotBlank()) { "Status text is required" }
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

    public suspend fun sendMedia(
        status: MediaStatus,
        attachments: List<CreateMediaV2Form>,
    ): SendResult {
        require(attachments.isNotEmpty()) { "At least one attachment is required" }
        require(attachments.size <= 4) { "At most 4 attachments are supported" }

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
