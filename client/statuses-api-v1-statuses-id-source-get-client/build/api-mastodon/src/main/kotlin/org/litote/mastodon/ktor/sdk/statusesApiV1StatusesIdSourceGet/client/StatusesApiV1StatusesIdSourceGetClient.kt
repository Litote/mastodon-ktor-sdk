package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdSourceGet.client

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
import org.litote.mastodon.ktor.sdk.model.StatusSource
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface StatusesApiV1StatusesIdSourceGetClient {
  /**
   * View status source
   */
  public suspend fun getStatusSource(id: String): GetStatusSourceResponse

  @Serializable
  public sealed class GetStatusSourceResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusSourceResponseSuccess(
    public val body: StatusSource,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusSourceResponse() {
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
  public data class GetStatusSourceResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusSourceResponse()

  @Serializable
  public data class GetStatusSourceResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusSourceResponse()

  @Serializable
  public data class GetStatusSourceResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusSourceResponse()

  @Serializable
  public data class GetStatusSourceResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusSourceResponse()
}

public fun StatusesApiV1StatusesIdSourceGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdSourceGetClient = DefaultStatusesApiV1StatusesIdSourceGetClient(configuration)

public class DefaultStatusesApiV1StatusesIdSourceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdSourceGetClient {
  override suspend fun getStatusSource(id: String): StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/source".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponseSuccess(response.body<StatusSource>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdSourceGetClient.GetStatusSourceResponseUnknownFailure(500)
    }
  }
}
