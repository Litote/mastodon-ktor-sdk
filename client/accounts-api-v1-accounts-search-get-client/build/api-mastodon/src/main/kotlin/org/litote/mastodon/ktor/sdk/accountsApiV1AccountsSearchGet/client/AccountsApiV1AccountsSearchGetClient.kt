package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsSearchGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
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

public interface AccountsApiV1AccountsSearchGetClient {
  /**
   * Search for matching accounts
   */
  public suspend fun getAccountSearch(
    q: String,
    following: Boolean? = false,
    limit: Long? = 40,
    offset: Long? = null,
    resolve: Boolean? = false,
  ): GetAccountSearchResponse

  @Serializable
  public sealed class GetAccountSearchResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountSearchResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountSearchResponse() {
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
  public data class GetAccountSearchResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountSearchResponse()

  @Serializable
  public data class GetAccountSearchResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountSearchResponse()

  @Serializable
  public data class GetAccountSearchResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountSearchResponse()

  @Serializable
  public data class GetAccountSearchResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountSearchResponse()
}

public fun AccountsApiV1AccountsSearchGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsSearchGetClient = DefaultAccountsApiV1AccountsSearchGetClient(configuration)

public class DefaultAccountsApiV1AccountsSearchGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsSearchGetClient {
  override suspend fun getAccountSearch(
    q: String,
    following: Boolean?,
    limit: Long?,
    offset: Long?,
    resolve: Boolean?,
  ): AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/search") {
        url {
          parameters.append("q", q)
          if (following != null) {
            parameters.append("following", following.toString())
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (offset != null) {
            parameters.append("offset", offset.toString())
          }
          if (resolve != null) {
            parameters.append("resolve", resolve.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsSearchGetClient.GetAccountSearchResponseUnknownFailure(500)
    }
  }
}
