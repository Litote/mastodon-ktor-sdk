package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnfavouritePost.client

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

public class StatusesApiV1StatusesIdUnfavouritePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Undo favourite of a status
   */
  public suspend fun postStatusUnfavourite(id: String): PostStatusUnfavouriteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unfavourite".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusUnfavouriteResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusUnfavouriteResponseFailure401(response.body<Error>())
        410 -> PostStatusUnfavouriteResponseFailure410
        422 -> PostStatusUnfavouriteResponseFailure(response.body<ValidationError>())
        else -> PostStatusUnfavouriteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusUnfavouriteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusUnfavouriteResponse

  @Serializable
  public data class PostStatusUnfavouriteResponseSuccess(
    public val body: Status,
  ) : PostStatusUnfavouriteResponse()

  @Serializable
  public data class PostStatusUnfavouriteResponseFailure401(
    public val body: Error,
  ) : PostStatusUnfavouriteResponse()

  @Serializable
  public object PostStatusUnfavouriteResponseFailure410 : PostStatusUnfavouriteResponse()

  @Serializable
  public data class PostStatusUnfavouriteResponseFailure(
    public val body: ValidationError,
  ) : PostStatusUnfavouriteResponse()

  @Serializable
  public data class PostStatusUnfavouriteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusUnfavouriteResponse()
}
