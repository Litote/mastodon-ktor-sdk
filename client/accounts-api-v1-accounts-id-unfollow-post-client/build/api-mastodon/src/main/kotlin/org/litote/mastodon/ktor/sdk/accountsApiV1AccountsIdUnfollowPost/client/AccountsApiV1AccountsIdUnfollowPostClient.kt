package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnfollowPost.client

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

public class AccountsApiV1AccountsIdUnfollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unfollow account
   */
  public suspend fun postAccountUnfollow(id: String): PostAccountUnfollowResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unfollow".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountUnfollowResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountUnfollowResponseFailure401(response.body<Error>())
        410 -> PostAccountUnfollowResponseFailure
        else -> PostAccountUnfollowResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountUnfollowResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountUnfollowResponse

  @Serializable
  public data class PostAccountUnfollowResponseSuccess(
    public val body: Relationship,
  ) : PostAccountUnfollowResponse()

  @Serializable
  public data class PostAccountUnfollowResponseFailure401(
    public val body: Error,
  ) : PostAccountUnfollowResponse()

  @Serializable
  public object PostAccountUnfollowResponseFailure : PostAccountUnfollowResponse()

  @Serializable
  public data class PostAccountUnfollowResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountUnfollowResponse()
}
