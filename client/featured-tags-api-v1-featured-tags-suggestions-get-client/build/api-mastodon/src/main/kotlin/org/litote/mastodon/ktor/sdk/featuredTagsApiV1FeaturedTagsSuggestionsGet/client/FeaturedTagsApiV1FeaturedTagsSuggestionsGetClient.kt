package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsSuggestionsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public class FeaturedTagsApiV1FeaturedTagsSuggestionsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View suggested tags to feature
   */
  public suspend fun getFeaturedTagSuggestions(): GetFeaturedTagSuggestionsResponse {
    try {
      val response = configuration.client.`get`("api/v1/featured_tags/suggestions") {
      }
      return when (response.status.value) {
        200 -> GetFeaturedTagSuggestionsResponseSuccess(response.body<List<Tag>>())
        401, 404, 429, 503 -> GetFeaturedTagSuggestionsResponseFailure401(response.body<Error>())
        410 -> GetFeaturedTagSuggestionsResponseFailure410
        422 -> GetFeaturedTagSuggestionsResponseFailure(response.body<ValidationError>())
        else -> GetFeaturedTagSuggestionsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFeaturedTagSuggestionsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFeaturedTagSuggestionsResponse

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseSuccess(
    public val body: List<Tag>,
  ) : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseFailure401(
    public val body: Error,
  ) : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public object GetFeaturedTagSuggestionsResponseFailure410 : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseFailure(
    public val body: ValidationError,
  ) : GetFeaturedTagSuggestionsResponse()

  @Serializable
  public data class GetFeaturedTagSuggestionsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFeaturedTagSuggestionsResponse()
}
