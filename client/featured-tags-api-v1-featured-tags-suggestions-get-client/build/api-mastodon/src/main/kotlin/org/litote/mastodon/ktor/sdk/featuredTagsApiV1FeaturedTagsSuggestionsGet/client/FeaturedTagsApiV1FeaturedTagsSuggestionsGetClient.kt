package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsSuggestionsGet.client

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
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public interface FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient {
  /**
   * View suggested tags to feature
   */
  public suspend fun getFeaturedTagSuggestions(): GetFeaturedTagSuggestionsResponse

  @Serializable
  public sealed class GetFeaturedTagSuggestionsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseSuccess(
    public val body: List<Tag>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagSuggestionsResponse() {
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
  public data class GetFeaturedTagSuggestionsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFeaturedTagSuggestionsResponse()
}

public fun FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient = DefaultFeaturedTagsApiV1FeaturedTagsSuggestionsGetClient(configuration)

public class DefaultFeaturedTagsApiV1FeaturedTagsSuggestionsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient {
  override suspend fun getFeaturedTagSuggestions(): FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponse {
    try {
      val response = configuration.client.`get`("api/v1/featured_tags/suggestions") {
      }
      return when (response.status.value) {
        200 -> FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponseSuccess(response.body<List<Tag>>(), response.headers)
        401, 404, 429, 503 -> FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponseFailure401(response.body<Error>(), response.headers)
        410 -> FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponseFailure410(response.headers)
        422 -> FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient.GetFeaturedTagSuggestionsResponseUnknownFailure(500)
    }
  }
}
