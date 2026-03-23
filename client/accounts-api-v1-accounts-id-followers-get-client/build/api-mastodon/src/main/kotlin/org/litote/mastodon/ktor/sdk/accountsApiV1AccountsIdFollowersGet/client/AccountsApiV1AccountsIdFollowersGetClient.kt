package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFollowersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
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

public class AccountsApiV1AccountsIdFollowersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get account's followers
   */
  public suspend fun getAccountFollowers(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetAccountFollowersResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/followers".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetAccountFollowersResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetAccountFollowersResponseFailure401(response.body<Error>())
        410 -> GetAccountFollowersResponseFailure410
        422 -> GetAccountFollowersResponseFailure(response.body<ValidationError>())
        else -> GetAccountFollowersResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountFollowersResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountFollowersResponse

  @Serializable
  public data class GetAccountFollowersResponseSuccess(
    public val body: List<Account>,
  ) : GetAccountFollowersResponse()

  @Serializable
  public data class GetAccountFollowersResponseFailure401(
    public val body: Error,
  ) : GetAccountFollowersResponse()

  @Serializable
  public object GetAccountFollowersResponseFailure410 : GetAccountFollowersResponse()

  @Serializable
  public data class GetAccountFollowersResponseFailure(
    public val body: ValidationError,
  ) : GetAccountFollowersResponse()

  @Serializable
  public data class GetAccountFollowersResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountFollowersResponse()
}
