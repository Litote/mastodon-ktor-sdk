package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.ScheduledStatus

public interface ScheduledStatusesApiV1ScheduledStatusesIdGetClient {
  /**
   * View a single scheduled status
   */
  public suspend fun getScheduledStatus(id: String): GetScheduledStatusResponse

  @Serializable
  public sealed class GetScheduledStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetScheduledStatusResponseSuccess(
    public val body: ScheduledStatus,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusResponse() {
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
  public data class GetScheduledStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusResponse()

  @Serializable
  public data class GetScheduledStatusResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusResponse()

  @Serializable
  public data class GetScheduledStatusResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusResponse()

  @Serializable
  public data class GetScheduledStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusResponse()
}

public fun ScheduledStatusesApiV1ScheduledStatusesIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): ScheduledStatusesApiV1ScheduledStatusesIdGetClient = DefaultScheduledStatusesApiV1ScheduledStatusesIdGetClient(configuration)

public class DefaultScheduledStatusesApiV1ScheduledStatusesIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ScheduledStatusesApiV1ScheduledStatusesIdGetClient {
  override suspend fun getScheduledStatus(id: String): ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponse {
    try {
      val response = configuration.client.`get`("api/v1/scheduled_statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponseSuccess(response.body<ScheduledStatus>(), response.headers)
        401, 404, 429, 503 -> ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponseFailure410(response.headers)
        422 -> ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ScheduledStatusesApiV1ScheduledStatusesIdGetClient.GetScheduledStatusResponseUnknownFailure(500)
    }
  }
}
