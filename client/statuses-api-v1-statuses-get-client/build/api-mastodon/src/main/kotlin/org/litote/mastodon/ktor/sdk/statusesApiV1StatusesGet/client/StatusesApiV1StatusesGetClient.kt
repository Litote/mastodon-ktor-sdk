package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesGetClient {
  /**
   * View multiple statuses
   */
  public suspend fun getStatuses(id: List<String>? = null): GetStatusesResponse

  @Serializable
  public sealed class GetStatusesResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusesResponseSuccess(
    public val body: List<Status>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse() {
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
  public data class GetStatusesResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()
}

public fun StatusesApiV1StatusesGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesGetClient = DefaultStatusesApiV1StatusesGetClient(configuration)

public class DefaultStatusesApiV1StatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesGetClient {
  override suspend fun getStatuses(id: List<String>?): StatusesApiV1StatusesGetClient.GetStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses") {
        url {
          if (id != null) {
            parameters.appendAll("id", id)
          }
        }
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesGetClient.GetStatusesResponseSuccess(response.body<List<Status>>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesGetClient.GetStatusesResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesGetClient.GetStatusesResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesGetClient.GetStatusesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesGetClient.GetStatusesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesGetClient.GetStatusesResponseUnknownFailure(500)
    }
  }
}
