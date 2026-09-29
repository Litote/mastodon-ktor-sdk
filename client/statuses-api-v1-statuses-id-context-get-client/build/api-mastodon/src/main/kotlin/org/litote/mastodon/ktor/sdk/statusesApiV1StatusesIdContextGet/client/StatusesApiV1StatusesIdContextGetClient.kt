package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdContextGet.client

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
import org.litote.mastodon.ktor.sdk.model.Context
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface StatusesApiV1StatusesIdContextGetClient {
  /**
   * Get parent and child statuses in context
   */
  public suspend fun getStatusContext(id: String): GetStatusContextResponse

  @Serializable
  public sealed class GetStatusContextResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusContextResponseSuccess(
    public val body: Context,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusContextResponse() {
    /**
     * Indicates an async refresh is in progress. Format: id="<string>", retry=<int>, result_count=<int>. The retry value indicates seconds to wait before retrying. The result_count is optional and indicates results already fetched.
     */
    public val mastodonAsyncRefresh: String?
      get() = headers["Mastodon-Async-Refresh"]

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
  public data class GetStatusContextResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusContextResponse()

  @Serializable
  public data class GetStatusContextResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusContextResponse()

  @Serializable
  public data class GetStatusContextResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusContextResponse()

  @Serializable
  public data class GetStatusContextResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusContextResponse()
}

public fun StatusesApiV1StatusesIdContextGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdContextGetClient = DefaultStatusesApiV1StatusesIdContextGetClient(configuration)

public class DefaultStatusesApiV1StatusesIdContextGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdContextGetClient {
  override suspend fun getStatusContext(id: String): StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/context".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponseSuccess(response.body<Context>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdContextGetClient.GetStatusContextResponseUnknownFailure(500)
    }
  }
}
