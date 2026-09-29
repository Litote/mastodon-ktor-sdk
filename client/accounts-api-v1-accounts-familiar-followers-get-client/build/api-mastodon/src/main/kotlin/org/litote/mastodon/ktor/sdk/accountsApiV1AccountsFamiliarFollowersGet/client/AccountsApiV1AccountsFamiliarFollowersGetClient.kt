package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsFamiliarFollowersGet.client

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
import org.litote.mastodon.ktor.sdk.model.FamiliarFollowers
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public interface AccountsApiV1AccountsFamiliarFollowersGetClient {
  /**
   * Find familiar followers
   */
  public suspend fun getAccountsFamiliarFollowers(id: List<String>? = null): GetAccountsFamiliarFollowersResponse

  @Serializable
  public sealed class GetAccountsFamiliarFollowersResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountsFamiliarFollowersResponseSuccess(
    public val body: List<FamiliarFollowers>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsFamiliarFollowersResponse() {
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
  public data class GetAccountsFamiliarFollowersResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsFamiliarFollowersResponse()

  @Serializable
  public data class GetAccountsFamiliarFollowersResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsFamiliarFollowersResponse()

  @Serializable
  public data class GetAccountsFamiliarFollowersResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountsFamiliarFollowersResponse()
}

public fun AccountsApiV1AccountsFamiliarFollowersGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsFamiliarFollowersGetClient = DefaultAccountsApiV1AccountsFamiliarFollowersGetClient(configuration)

public class DefaultAccountsApiV1AccountsFamiliarFollowersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsFamiliarFollowersGetClient {
  override suspend fun getAccountsFamiliarFollowers(id: List<String>?): AccountsApiV1AccountsFamiliarFollowersGetClient.GetAccountsFamiliarFollowersResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/familiar_followers") {
        url {
          if (id != null) {
            parameters.appendAll("id", id)
          }
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsFamiliarFollowersGetClient.GetAccountsFamiliarFollowersResponseSuccess(response.body<List<FamiliarFollowers>>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsFamiliarFollowersGetClient.GetAccountsFamiliarFollowersResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsFamiliarFollowersGetClient.GetAccountsFamiliarFollowersResponseFailure(response.headers)
        else -> AccountsApiV1AccountsFamiliarFollowersGetClient.GetAccountsFamiliarFollowersResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsFamiliarFollowersGetClient.GetAccountsFamiliarFollowersResponseUnknownFailure(500)
    }
  }
}
