package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.ScheduledStatus

public interface ScheduledStatusesApiV1ScheduledStatusesGetClient {
  /**
   * View scheduled statuses
   */
  public suspend fun getScheduledStatuses(
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetScheduledStatusesResponse

  @Serializable
  public sealed class GetScheduledStatusesResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetScheduledStatusesResponseSuccess(
    public val body: List<ScheduledStatus>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusesResponse() {
    /**
     * Pagination links for browsing older or newer results. Format: Link: <https://mastodon.example/api/v1/endpoint?max_id=7163058>; rel="next", <https://mastodon.example/api/v1/endpoint?min_id=7275607>; rel="prev". See [RFC 8288](https://www.rfc-editor.org/rfc/rfc8288) for more information.
     */
    public val link: String?
      get() = headers["Link"]

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
  public data class GetScheduledStatusesResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusesResponse()

  @Serializable
  public data class GetScheduledStatusesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusesResponse()

  @Serializable
  public data class GetScheduledStatusesResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusesResponse()

  @Serializable
  public data class GetScheduledStatusesResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetScheduledStatusesResponse()
}

public fun ScheduledStatusesApiV1ScheduledStatusesGetClient(configuration: ClientConfiguration = defaultClientConfiguration): ScheduledStatusesApiV1ScheduledStatusesGetClient = DefaultScheduledStatusesApiV1ScheduledStatusesGetClient(configuration)

public class DefaultScheduledStatusesApiV1ScheduledStatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ScheduledStatusesApiV1ScheduledStatusesGetClient {
  override suspend fun getScheduledStatuses(
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/scheduled_statuses") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponseSuccess(response.body<List<ScheduledStatus>>(), response.headers)
        401, 404, 429, 503 -> ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponseFailure401(response.body<Error>(), response.headers)
        410 -> ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponseFailure410(response.headers)
        422 -> ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ScheduledStatusesApiV1ScheduledStatusesGetClient.GetScheduledStatusesResponseUnknownFailure(500)
    }
  }
}
