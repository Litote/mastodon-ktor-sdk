package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
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

public interface AccountsApiV1AccountsIdGetClient {
  /**
   * Get account
   */
  public suspend fun getAccount(id: String): GetAccountResponse

  @Serializable
  public sealed class GetAccountResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountResponseSuccess(
    public val body: Account,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountResponse() {
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
  public data class GetAccountResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountResponse()

  @Serializable
  public data class GetAccountResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountResponse()

  @Serializable
  public data class GetAccountResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountResponse()

  @Serializable
  public data class GetAccountResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountResponse()
}

public fun AccountsApiV1AccountsIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdGetClient = DefaultAccountsApiV1AccountsIdGetClient(configuration)

public class DefaultAccountsApiV1AccountsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdGetClient {
  override suspend fun getAccount(id: String): AccountsApiV1AccountsIdGetClient.GetAccountResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdGetClient.GetAccountResponseSuccess(response.body<Account>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsIdGetClient.GetAccountResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdGetClient.GetAccountResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsIdGetClient.GetAccountResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsIdGetClient.GetAccountResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdGetClient.GetAccountResponseUnknownFailure(500)
    }
  }
}
