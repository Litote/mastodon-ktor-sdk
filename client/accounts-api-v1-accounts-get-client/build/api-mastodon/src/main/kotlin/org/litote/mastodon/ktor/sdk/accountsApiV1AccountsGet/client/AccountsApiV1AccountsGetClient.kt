package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class AccountsApiV1AccountsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get multiple accounts
   */
  public suspend fun getAccounts(id: List<String>? = null): GetAccountsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts") {
        url {
          if (id != null) {
            parameters.append("id", id.joinToString(","))
          }
        }
      }
      return when (response.status.value) {
        200 -> GetAccountsResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetAccountsResponseFailure401(response.body<Error>())
        410 -> GetAccountsResponseFailure410
        422 -> GetAccountsResponseFailure(response.body<ValidationError>())
        else -> GetAccountsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object Id

  @Serializable
  public sealed class GetAccountsResponse

  @Serializable
  public data class GetAccountsResponseSuccess(
    public val body: List<Account>,
  ) : GetAccountsResponse()

  @Serializable
  public data class GetAccountsResponseFailure401(
    public val body: Error,
  ) : GetAccountsResponse()

  @Serializable
  public object GetAccountsResponseFailure410 : GetAccountsResponse()

  @Serializable
  public data class GetAccountsResponseFailure(
    public val body: ValidationError,
  ) : GetAccountsResponse()

  @Serializable
  public data class GetAccountsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountsResponse()
}
