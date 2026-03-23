package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Edit a status
   */
  public suspend fun updateStatus(request: UpdateStatusRequest, id: String): UpdateStatusResponse {
    try {
      val response = configuration.client.put("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateStatusResponseSuccess(response.body<Status>())
        401, 404, 422, 429, 503 -> UpdateStatusResponseFailure401(response.body<Error>())
        410 -> UpdateStatusResponseFailure
        else -> UpdateStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateStatusRequest(
    public val language: String? = null,
    @SerialName("media_attributes[]")
    public val mediaAttributes: List<String>? = null,
    @SerialName("media_ids")
    public val mediaIds: List<String>? = null,
    public val poll: Poll? = null,
    @SerialName("quote_approval_policy")
    public val quoteApprovalPolicy: String? = null,
    public val sensitive: Boolean? = null,
    @SerialName("spoiler_text")
    public val spoilerText: String? = null,
    public val status: String? = null,
  ) {
    @Serializable
    public data class Poll(
      @SerialName("expires_in")
      public val expiresIn: Long? = null,
      @SerialName("hide_totals")
      public val hideTotals: Boolean? = null,
      public val multiple: Boolean? = null,
      public val options: List<String>? = null,
    )
  }

  @Serializable
  public sealed class UpdateStatusResponse

  @Serializable
  public data class UpdateStatusResponseSuccess(
    public val body: Status,
  ) : UpdateStatusResponse()

  @Serializable
  public data class UpdateStatusResponseFailure401(
    public val body: Error,
  ) : UpdateStatusResponse()

  @Serializable
  public object UpdateStatusResponseFailure : UpdateStatusResponse()

  @Serializable
  public data class UpdateStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateStatusResponse()
}
