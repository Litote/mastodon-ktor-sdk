package org.litote.mastodon.ktor.sdk.tagsApiV1TagsIdFeaturePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public class TagsApiV1TagsIdFeaturePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Feature a hashtag
   */
  public suspend fun postTagFeature(id: String): PostTagFeatureResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{id}/feature".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostTagFeatureResponseSuccess(response.body<Tag>())
        401, 404, 429, 503 -> PostTagFeatureResponseFailure401(response.body<Error>())
        410 -> PostTagFeatureResponseFailure410
        422 -> PostTagFeatureResponseFailure(response.body<ValidationError>())
        else -> PostTagFeatureResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostTagFeatureResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostTagFeatureResponse

  @Serializable
  public data class PostTagFeatureResponseSuccess(
    public val body: Tag,
  ) : PostTagFeatureResponse()

  @Serializable
  public data class PostTagFeatureResponseFailure401(
    public val body: Error,
  ) : PostTagFeatureResponse()

  @Serializable
  public object PostTagFeatureResponseFailure410 : PostTagFeatureResponse()

  @Serializable
  public data class PostTagFeatureResponseFailure(
    public val body: ValidationError,
  ) : PostTagFeatureResponse()

  @Serializable
  public data class PostTagFeatureResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostTagFeatureResponse()
}
