package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdEndorsementsGet.client

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

public class AccountsApiV1AccountsIdEndorsementsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get featured accounts
   */
  public suspend fun getAccountEndorsements(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetAccountEndorsementsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/endorsements".replace("/{id}", "/${id.encodeURLPathPart()}")) {
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
        200 -> GetAccountEndorsementsResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetAccountEndorsementsResponseFailure401(response.body<Error>())
        410 -> GetAccountEndorsementsResponseFailure410
        422 -> GetAccountEndorsementsResponseFailure(response.body<ValidationError>())
        else -> GetAccountEndorsementsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountEndorsementsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountEndorsementsResponse

  @Serializable
  public data class GetAccountEndorsementsResponseSuccess(
    public val body: List<Account>,
  ) : GetAccountEndorsementsResponse()

  @Serializable
  public data class GetAccountEndorsementsResponseFailure401(
    public val body: Error,
  ) : GetAccountEndorsementsResponse()

  @Serializable
  public object GetAccountEndorsementsResponseFailure410 : GetAccountEndorsementsResponse()

  @Serializable
  public data class GetAccountEndorsementsResponseFailure(
    public val body: ValidationError,
  ) : GetAccountEndorsementsResponse()

  @Serializable
  public data class GetAccountEndorsementsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountEndorsementsResponse()
}
