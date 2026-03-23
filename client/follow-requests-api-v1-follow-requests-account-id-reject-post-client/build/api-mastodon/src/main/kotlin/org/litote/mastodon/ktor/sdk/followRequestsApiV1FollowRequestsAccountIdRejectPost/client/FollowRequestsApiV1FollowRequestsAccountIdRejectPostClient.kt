package org.litote.mastodon.ktor.sdk.followRequestsApiV1FollowRequestsAccountIdRejectPost.client

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

public class FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Reject follow request
   */
  public suspend fun postFollowRequestReject(accountId: String): PostFollowRequestRejectResponse {
    try {
      val response = configuration.client.post("api/v1/follow_requests/{account_id}/reject".replace("/{account_id}", "/${accountId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostFollowRequestRejectResponseSuccess(response.body<Relationship>())
        401, 404, 429, 503 -> PostFollowRequestRejectResponseFailure401(response.body<Error>())
        410 -> PostFollowRequestRejectResponseFailure410
        422 -> PostFollowRequestRejectResponseFailure(response.body<ValidationError>())
        else -> PostFollowRequestRejectResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostFollowRequestRejectResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostFollowRequestRejectResponse

  @Serializable
  public data class PostFollowRequestRejectResponseSuccess(
    public val body: Relationship,
  ) : PostFollowRequestRejectResponse()

  @Serializable
  public data class PostFollowRequestRejectResponseFailure401(
    public val body: Error,
  ) : PostFollowRequestRejectResponse()

  @Serializable
  public object PostFollowRequestRejectResponseFailure410 : PostFollowRequestRejectResponse()

  @Serializable
  public data class PostFollowRequestRejectResponseFailure(
    public val body: ValidationError,
  ) : PostFollowRequestRejectResponse()

  @Serializable
  public data class PostFollowRequestRejectResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostFollowRequestRejectResponse()
}
