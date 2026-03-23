package org.litote.mastodon.ktor.sdk.tagsApiV1TagsNameUnfollowPost.client

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

public class TagsApiV1TagsNameUnfollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unfollow a hashtag
   */
  public suspend fun postTagUnfollow(name: String): PostTagUnfollowResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{name}/unfollow".replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostTagUnfollowResponseSuccess(response.body<Tag>())
        401, 404, 429, 503 -> PostTagUnfollowResponseFailure401(response.body<Error>())
        410 -> PostTagUnfollowResponseFailure410
        422 -> PostTagUnfollowResponseFailure(response.body<ValidationError>())
        else -> PostTagUnfollowResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostTagUnfollowResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostTagUnfollowResponse

  @Serializable
  public data class PostTagUnfollowResponseSuccess(
    public val body: Tag,
  ) : PostTagUnfollowResponse()

  @Serializable
  public data class PostTagUnfollowResponseFailure401(
    public val body: Error,
  ) : PostTagUnfollowResponse()

  @Serializable
  public object PostTagUnfollowResponseFailure410 : PostTagUnfollowResponse()

  @Serializable
  public data class PostTagUnfollowResponseFailure(
    public val body: ValidationError,
  ) : PostTagUnfollowResponse()

  @Serializable
  public data class PostTagUnfollowResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostTagUnfollowResponse()
}
