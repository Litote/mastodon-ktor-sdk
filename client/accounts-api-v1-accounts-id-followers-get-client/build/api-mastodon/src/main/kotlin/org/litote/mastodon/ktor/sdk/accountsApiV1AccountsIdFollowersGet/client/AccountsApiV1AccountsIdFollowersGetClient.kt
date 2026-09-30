package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFollowersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
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

public interface AccountsApiV1AccountsIdFollowersGetClient {
  /**
   * Get account's followers
   */
  public suspend fun getAccountFollowers(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetAccountFollowersResponse

  @Serializable
  public sealed class GetAccountFollowersResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountFollowersResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFollowersResponse() {
    /**
     * Pagination links for browsing older or newer results. Format: Link: <https://mastodon.example/api/v1/endpoint?max_id=7163058>; rel="next", <https://mastodon.example/api/v1/endpoint?min_id=7275607>; rel="prev". See [RFC 8288](https://www.rfc-editor.org/rfc/rfc8288) for more information.
     */
    public val link: String?
      get() = headers["Link"]

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
  public data class GetAccountFollowersResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFollowersResponse()

  @Serializable
  public data class GetAccountFollowersResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFollowersResponse()

  @Serializable
  public data class GetAccountFollowersResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFollowersResponse()

  @Serializable
  public data class GetAccountFollowersResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFollowersResponse()
}

public fun AccountsApiV1AccountsIdFollowersGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdFollowersGetClient = DefaultAccountsApiV1AccountsIdFollowersGetClient(configuration)

public class DefaultAccountsApiV1AccountsIdFollowersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdFollowersGetClient {
  override suspend fun getAccountFollowers(
    id: String,
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/followers".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdFollowersGetClient.GetAccountFollowersResponseUnknownFailure(500)
    }
  }
}
