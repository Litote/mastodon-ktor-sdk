package org.litote.mastodon.ktor.sdk.trendsApiV1TrendsTagsGet.client

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
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public interface TrendsApiV1TrendsTagsGetClient {
  /**
   * View trending tags
   */
  public suspend fun getTrendTags(limit: Long? = 10, offset: Long? = null): GetTrendTagsResponse

  @Serializable
  public sealed class GetTrendTagsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetTrendTagsResponseSuccess(
    public val body: List<Tag>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendTagsResponse() {
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
  public data class GetTrendTagsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendTagsResponse()

  @Serializable
  public data class GetTrendTagsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendTagsResponse()

  @Serializable
  public data class GetTrendTagsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendTagsResponse()

  @Serializable
  public data class GetTrendTagsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTrendTagsResponse()
}

public fun TrendsApiV1TrendsTagsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): TrendsApiV1TrendsTagsGetClient = DefaultTrendsApiV1TrendsTagsGetClient(configuration)

public class DefaultTrendsApiV1TrendsTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TrendsApiV1TrendsTagsGetClient {
  override suspend fun getTrendTags(limit: Long?, offset: Long?): TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/trends/tags") {
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
        200 -> TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponseSuccess(response.body<List<Tag>>(), response.headers)
        401, 404, 429, 503 -> TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponseFailure401(response.body<Error>(), response.headers)
        410 -> TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponseFailure410(response.headers)
        422 -> TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TrendsApiV1TrendsTagsGetClient.GetTrendTagsResponseUnknownFailure(500)
    }
  }
}
