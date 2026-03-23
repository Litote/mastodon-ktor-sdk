package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdRemoveFromFollowersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsIdRemoveFromFollowersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove account from followers
   */
  public suspend fun postAccountRemoveFromFollowers(id: String): PostAccountRemoveFromFollowersResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/remove_from_followers".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountRemoveFromFollowersResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountRemoveFromFollowersResponseFailure401(response.body<Error>())
        410 -> PostAccountRemoveFromFollowersResponseFailure
        else -> PostAccountRemoveFromFollowersResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountRemoveFromFollowersResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountRemoveFromFollowersResponse

  @Serializable
  public data class PostAccountRemoveFromFollowersResponseSuccess(
    public val body: Relationship,
  ) : PostAccountRemoveFromFollowersResponse()

  @Serializable
  public data class PostAccountRemoveFromFollowersResponseFailure401(
    public val body: Error,
  ) : PostAccountRemoveFromFollowersResponse()

  @Serializable
  public object PostAccountRemoveFromFollowersResponseFailure : PostAccountRemoveFromFollowersResponse()

  @Serializable
  public data class PostAccountRemoveFromFollowersResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountRemoveFromFollowersResponse()
}
