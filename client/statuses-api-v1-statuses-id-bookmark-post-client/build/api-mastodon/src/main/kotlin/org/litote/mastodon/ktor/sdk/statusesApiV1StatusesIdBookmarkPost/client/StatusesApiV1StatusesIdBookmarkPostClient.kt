package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdBookmarkPost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdBookmarkPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Bookmark a status
   */
  public suspend fun postStatusBookmark(id: String): PostStatusBookmarkResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/bookmark".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusBookmarkResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusBookmarkResponseFailure401(response.body<Error>())
        410 -> PostStatusBookmarkResponseFailure410
        422 -> PostStatusBookmarkResponseFailure(response.body<ValidationError>())
        else -> PostStatusBookmarkResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusBookmarkResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusBookmarkResponse

  @Serializable
  public data class PostStatusBookmarkResponseSuccess(
    public val body: Status,
  ) : PostStatusBookmarkResponse()

  @Serializable
  public data class PostStatusBookmarkResponseFailure401(
    public val body: Error,
  ) : PostStatusBookmarkResponse()

  @Serializable
  public object PostStatusBookmarkResponseFailure410 : PostStatusBookmarkResponse()

  @Serializable
  public data class PostStatusBookmarkResponseFailure(
    public val body: ValidationError,
  ) : PostStatusBookmarkResponse()

  @Serializable
  public data class PostStatusBookmarkResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusBookmarkResponse()
}
