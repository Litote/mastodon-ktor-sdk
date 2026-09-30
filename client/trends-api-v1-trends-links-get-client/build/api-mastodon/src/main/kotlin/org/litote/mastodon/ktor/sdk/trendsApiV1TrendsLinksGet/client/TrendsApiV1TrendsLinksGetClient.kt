package org.litote.mastodon.ktor.sdk.trendsApiV1TrendsLinksGet.client

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
import org.litote.mastodon.ktor.sdk.model.TrendsLink
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface TrendsApiV1TrendsLinksGetClient {
  /**
   * View trending links
   */
  public suspend fun getTrendLinks(limit: Long? = 10, offset: Long? = null): GetTrendLinksResponse

  @Serializable
  public sealed class GetTrendLinksResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetTrendLinksResponseSuccess(
    public val body: List<TrendsLink>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendLinksResponse() {
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
  public data class GetTrendLinksResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendLinksResponse()

  @Serializable
  public data class GetTrendLinksResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendLinksResponse()

  @Serializable
  public data class GetTrendLinksResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendLinksResponse()

  @Serializable
  public data class GetTrendLinksResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendLinksResponse()
}

public fun TrendsApiV1TrendsLinksGetClient(configuration: ClientConfiguration = defaultClientConfiguration): TrendsApiV1TrendsLinksGetClient = DefaultTrendsApiV1TrendsLinksGetClient(configuration)

public class DefaultTrendsApiV1TrendsLinksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TrendsApiV1TrendsLinksGetClient {
  override suspend fun getTrendLinks(limit: Long?, offset: Long?): TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponse {
    try {
      val response = configuration.client.`get`("api/v1/trends/links") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (offset != null) {
            parameters.append("offset", offset.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponseSuccess(response.body<List<TrendsLink>>(), response.headers)
        401, 404, 429, 503 -> TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponseFailure401(response.body<Error>(), response.headers)
        410 -> TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponseFailure410(response.headers)
        422 -> TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TrendsApiV1TrendsLinksGetClient.GetTrendLinksResponseUnknownFailure(500)
    }
  }
}
