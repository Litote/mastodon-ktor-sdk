package org.litote.mastodon.ktor.sdk.followRequestsApiV1FollowRequestsAccountIdAuthorizePost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Accept follow request
   */
  public suspend fun postFollowRequestAuthorize(accountId: String): PostFollowRequestAuthorizeResponse {
    try {
      val response = configuration.client.post("api/v1/follow_requests/{account_id}/authorize".replace("/{account_id}", "/${accountId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostFollowRequestAuthorizeResponseSuccess(response.body<Relationship>())
        401, 404, 429, 503 -> PostFollowRequestAuthorizeResponseFailure401(response.body<Error>())
        410 -> PostFollowRequestAuthorizeResponseFailure410
        422 -> PostFollowRequestAuthorizeResponseFailure(response.body<ValidationError>())
        else -> PostFollowRequestAuthorizeResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostFollowRequestAuthorizeResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostFollowRequestAuthorizeResponse

  @Serializable
  public data class PostFollowRequestAuthorizeResponseSuccess(
    public val body: Relationship,
  ) : PostFollowRequestAuthorizeResponse()

  @Serializable
  public data class PostFollowRequestAuthorizeResponseFailure401(
    public val body: Error,
  ) : PostFollowRequestAuthorizeResponse()

  @Serializable
  public object PostFollowRequestAuthorizeResponseFailure410 : PostFollowRequestAuthorizeResponse()

  @Serializable
  public data class PostFollowRequestAuthorizeResponseFailure(
    public val body: ValidationError,
  ) : PostFollowRequestAuthorizeResponse()

  @Serializable
  public data class PostFollowRequestAuthorizeResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostFollowRequestAuthorizeResponse()
}
