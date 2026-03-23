package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsFamiliarFollowersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.FamiliarFollowers
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class AccountsApiV1AccountsFamiliarFollowersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Find familiar followers
   */
  public suspend fun getAccountsFamiliarFollowers(id: List<String>? = null): GetAccountsFamiliarFollowersResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/familiar_followers") {
        url {
          if (id != null) {
            parameters.append("id", id.joinToString(","))
          }
        }
      }
      return when (response.status.value) {
        200 -> GetAccountsFamiliarFollowersResponseSuccess(response.body<List<FamiliarFollowers>>())
        401, 404, 422, 429, 503 -> GetAccountsFamiliarFollowersResponseFailure401(response.body<Error>())
        410 -> GetAccountsFamiliarFollowersResponseFailure
        else -> GetAccountsFamiliarFollowersResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountsFamiliarFollowersResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object Id

  @Serializable
  public sealed class GetAccountsFamiliarFollowersResponse

  @Serializable
  public data class GetAccountsFamiliarFollowersResponseSuccess(
    public val body: List<FamiliarFollowers>,
  ) : GetAccountsFamiliarFollowersResponse()

  @Serializable
  public data class GetAccountsFamiliarFollowersResponseFailure401(
    public val body: Error,
  ) : GetAccountsFamiliarFollowersResponse()

  @Serializable
  public object GetAccountsFamiliarFollowersResponseFailure : GetAccountsFamiliarFollowersResponse()

  @Serializable
  public data class GetAccountsFamiliarFollowersResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountsFamiliarFollowersResponse()
}
