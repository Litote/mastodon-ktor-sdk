package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsVerifyCredentialsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsupdatecredentialspatch02a06ece.model.CredentialAccount

public interface AccountsApiV1AccountsVerifyCredentialsGetClient {
  /**
   * Verify account credentials
   */
  public suspend fun getAccountsVerifyCredentials(): GetAccountsVerifyCredentialsResponse

  @Serializable
  public sealed class GetAccountsVerifyCredentialsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseSuccess(
    public val body: CredentialAccount,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsVerifyCredentialsResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsVerifyCredentialsResponse()

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsVerifyCredentialsResponse()

  @Serializable
  public data class GetAccountsVerifyCredentialsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsVerifyCredentialsResponse()
}

public fun AccountsApiV1AccountsVerifyCredentialsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsVerifyCredentialsGetClient = DefaultAccountsApiV1AccountsVerifyCredentialsGetClient(configuration)

public class DefaultAccountsApiV1AccountsVerifyCredentialsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsVerifyCredentialsGetClient {
  override suspend fun getAccountsVerifyCredentials(): AccountsApiV1AccountsVerifyCredentialsGetClient.GetAccountsVerifyCredentialsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/verify_credentials") {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsVerifyCredentialsGetClient.GetAccountsVerifyCredentialsResponseSuccess(response.body<CredentialAccount>(), response.headers)
        401, 403, 404, 422, 429, 503 -> AccountsApiV1AccountsVerifyCredentialsGetClient.GetAccountsVerifyCredentialsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsVerifyCredentialsGetClient.GetAccountsVerifyCredentialsResponseFailure(response.headers)
        else -> AccountsApiV1AccountsVerifyCredentialsGetClient.GetAccountsVerifyCredentialsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsVerifyCredentialsGetClient.GetAccountsVerifyCredentialsResponseUnknownFailure(500)
    }
  }
}
