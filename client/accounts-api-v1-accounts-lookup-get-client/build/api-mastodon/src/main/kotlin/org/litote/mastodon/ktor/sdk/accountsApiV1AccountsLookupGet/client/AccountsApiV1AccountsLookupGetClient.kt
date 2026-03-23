package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsLookupGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class AccountsApiV1AccountsLookupGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Lookup account ID from WebFinger address
   */
  public suspend fun getAccountLookup(acct: String): GetAccountLookupResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/lookup") {
        url {
          parameters.append("acct", acct)
        }
      }
      return when (response.status.value) {
        200 -> GetAccountLookupResponseSuccess(response.body<Account>())
        401, 404, 429, 503 -> GetAccountLookupResponseFailure401(response.body<Error>())
        410 -> GetAccountLookupResponseFailure410
        422 -> GetAccountLookupResponseFailure(response.body<ValidationError>())
        else -> GetAccountLookupResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountLookupResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountLookupResponse

  @Serializable
  public data class GetAccountLookupResponseSuccess(
    public val body: Account,
  ) : GetAccountLookupResponse()

  @Serializable
  public data class GetAccountLookupResponseFailure401(
    public val body: Error,
  ) : GetAccountLookupResponse()

  @Serializable
  public object GetAccountLookupResponseFailure410 : GetAccountLookupResponse()

  @Serializable
  public data class GetAccountLookupResponseFailure(
    public val body: ValidationError,
  ) : GetAccountLookupResponse()

  @Serializable
  public data class GetAccountLookupResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountLookupResponse()
}
