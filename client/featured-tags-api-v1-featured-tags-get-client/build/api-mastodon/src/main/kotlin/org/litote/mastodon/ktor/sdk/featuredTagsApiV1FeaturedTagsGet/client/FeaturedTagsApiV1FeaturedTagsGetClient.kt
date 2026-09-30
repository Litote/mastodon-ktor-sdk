package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidfeaturedtagsgetB2ed9160.model.FeaturedTag

public interface FeaturedTagsApiV1FeaturedTagsGetClient {
  /**
   * View your featured tags
   */
  public suspend fun getFeaturedTags(): GetFeaturedTagsResponse

  @Serializable
  public sealed class GetFeaturedTagsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFeaturedTagsResponseSuccess(
    public val body: List<FeaturedTag>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagsResponse() {
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
  public data class GetFeaturedTagsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagsResponse()

  @Serializable
  public data class GetFeaturedTagsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagsResponse()

  @Serializable
  public data class GetFeaturedTagsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagsResponse()

  @Serializable
  public data class GetFeaturedTagsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagsResponse()
}

public fun FeaturedTagsApiV1FeaturedTagsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FeaturedTagsApiV1FeaturedTagsGetClient = DefaultFeaturedTagsApiV1FeaturedTagsGetClient(configuration)

public class DefaultFeaturedTagsApiV1FeaturedTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FeaturedTagsApiV1FeaturedTagsGetClient {
  override suspend fun getFeaturedTags(): FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/featured_tags") {
      }
      return when (response.status.value) {
        200 -> FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponseSuccess(response.body<List<FeaturedTag>>(), response.headers)
        401, 404, 429, 503 -> FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponseFailure401(response.body<Error>(), response.headers)
        410 -> FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponseFailure410(response.headers)
        422 -> FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FeaturedTagsApiV1FeaturedTagsGetClient.GetFeaturedTagsResponseUnknownFailure(500)
    }
  }
}
