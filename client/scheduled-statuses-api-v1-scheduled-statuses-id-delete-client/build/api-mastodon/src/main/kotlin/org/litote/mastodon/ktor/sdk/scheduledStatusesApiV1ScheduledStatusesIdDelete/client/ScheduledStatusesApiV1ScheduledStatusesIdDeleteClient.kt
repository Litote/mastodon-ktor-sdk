package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
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

public interface ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient {
  /**
   * Cancel a scheduled status
   */
  public suspend fun deleteScheduledStatus(id: String): DeleteScheduledStatusResponse

  @Serializable
  public sealed class DeleteScheduledStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteScheduledStatusResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteScheduledStatusResponse() {
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
  public data class DeleteScheduledStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteScheduledStatusResponse()

  @Serializable
  public data class DeleteScheduledStatusResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteScheduledStatusResponse()

  @Serializable
  public data class DeleteScheduledStatusResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteScheduledStatusResponse()

  @Serializable
  public data class DeleteScheduledStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteScheduledStatusResponse()
}

public fun ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient = DefaultScheduledStatusesApiV1ScheduledStatusesIdDeleteClient(configuration)

public class DefaultScheduledStatusesApiV1ScheduledStatusesIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient {
  override suspend fun deleteScheduledStatus(id: String): ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponse {
    try {
      val response = configuration.client.delete("api/v1/scheduled_statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponseSuccess(response.headers)
        401, 404, 429, 503 -> ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponseFailure410(response.headers)
        422 -> ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient.DeleteScheduledStatusResponseUnknownFailure(500)
    }
  }
}
