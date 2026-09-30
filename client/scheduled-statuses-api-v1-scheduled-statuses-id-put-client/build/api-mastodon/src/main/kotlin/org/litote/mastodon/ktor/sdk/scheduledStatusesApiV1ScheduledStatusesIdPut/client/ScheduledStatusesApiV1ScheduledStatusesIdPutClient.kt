package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.ScheduledStatus

public interface ScheduledStatusesApiV1ScheduledStatusesIdPutClient {
  /**
   * Update a scheduled status's publishing date
   */
  public suspend fun updateScheduledStatus(request: UpdateScheduledStatusRequest, id: String): UpdateScheduledStatusResponse

  @Serializable
  public data class UpdateScheduledStatusRequest(
    @SerialName("scheduled_at")
    public val scheduledAt: String? = null,
  )

  @Serializable
  public sealed class UpdateScheduledStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateScheduledStatusResponseSuccess(
    public val body: ScheduledStatus,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateScheduledStatusResponse() {
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
  public data class UpdateScheduledStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateScheduledStatusResponse()

  @Serializable
  public data class UpdateScheduledStatusResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateScheduledStatusResponse()

  @Serializable
  public data class UpdateScheduledStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateScheduledStatusResponse()
}

public fun ScheduledStatusesApiV1ScheduledStatusesIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): ScheduledStatusesApiV1ScheduledStatusesIdPutClient = DefaultScheduledStatusesApiV1ScheduledStatusesIdPutClient(configuration)

public class DefaultScheduledStatusesApiV1ScheduledStatusesIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ScheduledStatusesApiV1ScheduledStatusesIdPutClient {
  override suspend fun updateScheduledStatus(request: ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusRequest, id: String): ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusResponse {
    try {
      val response = configuration.client.put("api/v1/scheduled_statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusResponseSuccess(response.body<ScheduledStatus>(), response.headers)
        401, 404, 422, 429, 503 -> ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusResponseFailure(response.headers)
        else -> ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ScheduledStatusesApiV1ScheduledStatusesIdPutClient.UpdateScheduledStatusResponseUnknownFailure(500)
    }
  }
}
