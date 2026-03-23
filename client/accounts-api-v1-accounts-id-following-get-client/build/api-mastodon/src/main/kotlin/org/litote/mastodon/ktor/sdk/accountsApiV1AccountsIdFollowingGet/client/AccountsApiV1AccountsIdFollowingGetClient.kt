package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFollowingGet.client

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

public class AccountsApiV1AccountsIdFollowingGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get account's following
   */
  public suspend fun getAccountFollowing(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetAccountFollowingResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/following".replace("/{id}", "/${id.encodeURLPathPart()}")) {
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
        200 -> GetAccountFollowingResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetAccountFollowingResponseFailure401(response.body<Error>())
        410 -> GetAccountFollowingResponseFailure410
        422 -> GetAccountFollowingResponseFailure(response.body<ValidationError>())
        else -> GetAccountFollowingResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountFollowingResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountFollowingResponse

  @Serializable
  public data class GetAccountFollowingResponseSuccess(
    public val body: List<Account>,
  ) : GetAccountFollowingResponse()

  @Serializable
  public data class GetAccountFollowingResponseFailure401(
    public val body: Error,
  ) : GetAccountFollowingResponse()

  @Serializable
  public object GetAccountFollowingResponseFailure410 : GetAccountFollowingResponse()

  @Serializable
  public data class GetAccountFollowingResponseFailure(
    public val body: ValidationError,
  ) : GetAccountFollowingResponse()

  @Serializable
  public data class GetAccountFollowingResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountFollowingResponse()
}
