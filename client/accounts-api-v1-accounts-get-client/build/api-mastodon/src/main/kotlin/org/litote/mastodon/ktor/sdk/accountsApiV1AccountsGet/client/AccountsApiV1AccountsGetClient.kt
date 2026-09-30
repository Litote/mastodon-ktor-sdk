package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface AccountsApiV1AccountsGetClient {
  /**
   * Get multiple accounts
   */
  public suspend fun getAccounts(id: List<String>? = null): GetAccountsResponse

  @Serializable
  public sealed class GetAccountsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountsResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsResponse() {
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
  public data class GetAccountsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsResponse()

  @Serializable
  public data class GetAccountsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsResponse()

  @Serializable
  public data class GetAccountsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsResponse()

  @Serializable
  public data class GetAccountsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsResponse()
}

public fun AccountsApiV1AccountsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsGetClient = DefaultAccountsApiV1AccountsGetClient(configuration)

public class DefaultAccountsApiV1AccountsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsGetClient {
  override suspend fun getAccounts(id: List<String>?): AccountsApiV1AccountsGetClient.GetAccountsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts") {
        url {
          if (id != null) {
            parameters.appendAll("id", id)
          }
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsGetClient.GetAccountsResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsGetClient.GetAccountsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsGetClient.GetAccountsResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsGetClient.GetAccountsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsGetClient.GetAccountsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsGetClient.GetAccountsResponseUnknownFailure(500)
    }
  }
}
