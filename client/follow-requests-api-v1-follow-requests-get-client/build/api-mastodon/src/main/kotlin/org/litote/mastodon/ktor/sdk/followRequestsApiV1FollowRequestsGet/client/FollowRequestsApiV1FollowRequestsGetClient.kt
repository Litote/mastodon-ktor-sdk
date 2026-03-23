package org.litote.mastodon.ktor.sdk.followRequestsApiV1FollowRequestsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class FollowRequestsApiV1FollowRequestsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View pending follow requests
   */
  public suspend fun getFollowRequests(
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetFollowRequestsResponse {
    try {
      val response = configuration.client.`get`("api/v1/follow_requests") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetFollowRequestsResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetFollowRequestsResponseFailure401(response.body<Error>())
        410 -> GetFollowRequestsResponseFailure410
        422 -> GetFollowRequestsResponseFailure(response.body<ValidationError>())
        else -> GetFollowRequestsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFollowRequestsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFollowRequestsResponse

  @Serializable
  public data class GetFollowRequestsResponseSuccess(
    public val body: List<Account>,
  ) : GetFollowRequestsResponse()

  @Serializable
  public data class GetFollowRequestsResponseFailure401(
    public val body: Error,
  ) : GetFollowRequestsResponse()

  @Serializable
  public object GetFollowRequestsResponseFailure410 : GetFollowRequestsResponse()

  @Serializable
  public data class GetFollowRequestsResponseFailure(
    public val body: ValidationError,
  ) : GetFollowRequestsResponse()

  @Serializable
  public data class GetFollowRequestsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFollowRequestsResponse()
}
