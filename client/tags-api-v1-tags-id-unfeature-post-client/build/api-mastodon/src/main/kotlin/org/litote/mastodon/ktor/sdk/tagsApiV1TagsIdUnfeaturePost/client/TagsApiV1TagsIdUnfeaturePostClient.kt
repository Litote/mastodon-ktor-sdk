package org.litote.mastodon.ktor.sdk.tagsApiV1TagsIdUnfeaturePost.client

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

public class TagsApiV1TagsIdUnfeaturePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unfeature a hashtag
   */
  public suspend fun postTagUnfeature(id: String): PostTagUnfeatureResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{id}/unfeature".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostTagUnfeatureResponseSuccess(response.body<Tag>())
        401, 404, 429, 503 -> PostTagUnfeatureResponseFailure401(response.body<Error>())
        410 -> PostTagUnfeatureResponseFailure410
        422 -> PostTagUnfeatureResponseFailure(response.body<ValidationError>())
        else -> PostTagUnfeatureResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostTagUnfeatureResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostTagUnfeatureResponse

  @Serializable
  public data class PostTagUnfeatureResponseSuccess(
    public val body: Tag,
  ) : PostTagUnfeatureResponse()

  @Serializable
  public data class PostTagUnfeatureResponseFailure401(
    public val body: Error,
  ) : PostTagUnfeatureResponse()

  @Serializable
  public object PostTagUnfeatureResponseFailure410 : PostTagUnfeatureResponse()

  @Serializable
  public data class PostTagUnfeatureResponseFailure(
    public val body: ValidationError,
  ) : PostTagUnfeatureResponse()

  @Serializable
  public data class PostTagUnfeatureResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostTagUnfeatureResponse()
}
