package org.litote.mastodon.ktor.sdk.tagsApiV1TagsNameFollowPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public class TagsApiV1TagsNameFollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Follow a hashtag
   */
  public suspend fun postTagFollow(name: String): PostTagFollowResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{name}/follow".replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostTagFollowResponseSuccess(response.body<Tag>())
        401, 404, 422, 429, 503 -> PostTagFollowResponseFailure401(response.body<Error>())
        410 -> PostTagFollowResponseFailure
        else -> PostTagFollowResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostTagFollowResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostTagFollowResponse

  @Serializable
  public data class PostTagFollowResponseSuccess(
    public val body: Tag,
  ) : PostTagFollowResponse()

  @Serializable
  public data class PostTagFollowResponseFailure401(
    public val body: Error,
  ) : PostTagFollowResponse()

  @Serializable
  public object PostTagFollowResponseFailure : PostTagFollowResponse()

  @Serializable
  public data class PostTagFollowResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostTagFollowResponse()
}
