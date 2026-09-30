package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsLookupGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface AccountsApiV1AccountsLookupGetClient {
  /**
   * Lookup account ID from WebFinger address
   */
  public suspend fun getAccountLookup(acct: String): GetAccountLookupResponse

  @Serializable
  public sealed class GetAccountLookupResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountLookupResponseSuccess(
    public val body: Account,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountLookupResponse() {
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
  public data class GetAccountLookupResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountLookupResponse()

  @Serializable
  public data class GetAccountLookupResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountLookupResponse()

  @Serializable
  public data class GetAccountLookupResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountLookupResponse()

  @Serializable
  public data class GetAccountLookupResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountLookupResponse()
}

public fun AccountsApiV1AccountsLookupGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsLookupGetClient = DefaultAccountsApiV1AccountsLookupGetClient(configuration)

public class DefaultAccountsApiV1AccountsLookupGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsLookupGetClient {
  override suspend fun getAccountLookup(acct: String): AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/lookup") {
        url {
          parameters.append("acct", acct)
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponseSuccess(response.body<Account>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsLookupGetClient.GetAccountLookupResponseUnknownFailure(500)
    }
  }
}
