package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdPutClient {
  /**
   * Edit a status
   */
  public suspend fun updateStatus(request: UpdateStatusRequest, id: String): UpdateStatusResponse

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
  public sealed class UpdateStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateStatusResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class UpdateStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse()

  @Serializable
  public data class UpdateStatusResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse()

  @Serializable
  public data class UpdateStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse()
}

public fun StatusesApiV1StatusesIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdPutClient = DefaultStatusesApiV1StatusesIdPutClient(configuration)

public class DefaultStatusesApiV1StatusesIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdPutClient {
  override suspend fun updateStatus(request: StatusesApiV1StatusesIdPutClient.UpdateStatusRequest, id: String): StatusesApiV1StatusesIdPutClient.UpdateStatusResponse {
    try {
      val response = configuration.client.put("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdPutClient.UpdateStatusResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 422, 429, 503 -> StatusesApiV1StatusesIdPutClient.UpdateStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdPutClient.UpdateStatusResponseFailure(response.headers)
        else -> StatusesApiV1StatusesIdPutClient.UpdateStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdPutClient.UpdateStatusResponseUnknownFailure(500)
    }
  }
}
