package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsVerifyCredentialsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsupdatecredentialspatch02a06ece.model.CredentialAccount

public class AccountsApiV1AccountsVerifyCredentialsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Verify account credentials
   */
  public suspend fun getAccountsVerifyCredentials(): GetAccountsVerifyCredentialsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/verify_credentials") {
      }
      return when (response.status.value) {
        200 -> GetAccountsVerifyCredentialsResponseSuccess(response.body<CredentialAccount>())
        401, 403, 404, 422, 429, 503 -> GetAccountsVerifyCredentialsResponseFailure401(response.body<Error>())
        410 -> GetAccountsVerifyCredentialsResponseFailure
        else -> GetAccountsVerifyCredentialsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountsVerifyCredentialsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountsVerifyCredentialsResponse

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseSuccess(
    public val body: CredentialAccount,
  ) : GetAccountsVerifyCredentialsResponse()

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseFailure401(
    public val body: Error,
  ) : GetAccountsVerifyCredentialsResponse()

  @Serializable
  public object GetAccountsVerifyCredentialsResponseFailure : GetAccountsVerifyCredentialsResponse()

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountsVerifyCredentialsResponse()
}
