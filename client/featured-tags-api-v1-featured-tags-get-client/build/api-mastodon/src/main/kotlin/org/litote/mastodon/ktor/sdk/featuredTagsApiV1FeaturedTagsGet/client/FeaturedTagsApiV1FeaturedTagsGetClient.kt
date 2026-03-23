package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidfeaturedtagsgetB2ed9160.model.FeaturedTag

public class FeaturedTagsApiV1FeaturedTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View your featured tags
   */
  public suspend fun getFeaturedTags(): GetFeaturedTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/featured_tags") {
      }
      return when (response.status.value) {
        200 -> GetFeaturedTagsResponseSuccess(response.body<List<FeaturedTag>>())
        401, 404, 429, 503 -> GetFeaturedTagsResponseFailure401(response.body<Error>())
        410 -> GetFeaturedTagsResponseFailure410
        422 -> GetFeaturedTagsResponseFailure(response.body<ValidationError>())
        else -> GetFeaturedTagsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFeaturedTagsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFeaturedTagsResponse

  @Serializable
  public data class GetFeaturedTagsResponseSuccess(
    public val body: List<FeaturedTag>,
  ) : GetFeaturedTagsResponse()

  @Serializable
  public data class GetFeaturedTagsResponseFailure401(
    public val body: Error,
  ) : GetFeaturedTagsResponse()

  @Serializable
  public object GetFeaturedTagsResponseFailure410 : GetFeaturedTagsResponse()

  @Serializable
  public data class GetFeaturedTagsResponseFailure(
    public val body: ValidationError,
  ) : GetFeaturedTagsResponse()

  @Serializable
  public data class GetFeaturedTagsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFeaturedTagsResponse()
}
