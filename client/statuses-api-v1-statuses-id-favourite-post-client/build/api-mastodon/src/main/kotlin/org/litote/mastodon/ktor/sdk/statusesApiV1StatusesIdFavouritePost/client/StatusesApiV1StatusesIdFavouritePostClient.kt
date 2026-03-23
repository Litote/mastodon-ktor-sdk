package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdFavouritePost.client

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

public class StatusesApiV1StatusesIdFavouritePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Favourite a status
   */
  public suspend fun postStatusFavourite(id: String): PostStatusFavouriteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/favourite".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusFavouriteResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusFavouriteResponseFailure401(response.body<Error>())
        410 -> PostStatusFavouriteResponseFailure410
        422 -> PostStatusFavouriteResponseFailure(response.body<ValidationError>())
        else -> PostStatusFavouriteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusFavouriteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusFavouriteResponse

  @Serializable
  public data class PostStatusFavouriteResponseSuccess(
    public val body: Status,
  ) : PostStatusFavouriteResponse()

  @Serializable
  public data class PostStatusFavouriteResponseFailure401(
    public val body: Error,
  ) : PostStatusFavouriteResponse()

  @Serializable
  public object PostStatusFavouriteResponseFailure410 : PostStatusFavouriteResponse()

  @Serializable
  public data class PostStatusFavouriteResponseFailure(
    public val body: ValidationError,
  ) : PostStatusFavouriteResponse()

  @Serializable
  public data class PostStatusFavouriteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusFavouriteResponse()
}
