package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class AccountsApiV1AccountsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get account
   */
  public suspend fun getAccount(id: String): GetAccountResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetAccountResponseSuccess(response.body<Account>())
        401, 404, 429, 503 -> GetAccountResponseFailure401(response.body<Error>())
        410 -> GetAccountResponseFailure410
        422 -> GetAccountResponseFailure(response.body<ValidationError>())
        else -> GetAccountResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountResponse

  @Serializable
  public data class GetAccountResponseSuccess(
    public val body: Account,
  ) : GetAccountResponse()

  @Serializable
  public data class GetAccountResponseFailure401(
    public val body: Error,
  ) : GetAccountResponse()

  @Serializable
  public object GetAccountResponseFailure410 : GetAccountResponse()

  @Serializable
  public data class GetAccountResponseFailure(
    public val body: ValidationError,
  ) : GetAccountResponse()

  @Serializable
  public data class GetAccountResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountResponse()
}
