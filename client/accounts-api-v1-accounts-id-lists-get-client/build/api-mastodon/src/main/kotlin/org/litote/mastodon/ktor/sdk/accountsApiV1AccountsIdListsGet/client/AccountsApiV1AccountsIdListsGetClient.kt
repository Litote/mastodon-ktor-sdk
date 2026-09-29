package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdListsGet.client

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
import kotlin.collections.List as CollectionsList
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List as ModelList

public interface AccountsApiV1AccountsIdListsGetClient {
  /**
   * Get lists containing this account
   */
  public suspend fun getAccountLists(id: String): GetAccountListsResponse

  @Serializable
  public sealed class GetAccountListsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountListsResponseSuccess(
    public val body: CollectionsList<ModelList>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountListsResponse() {
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
  public data class GetAccountListsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountListsResponse()

  @Serializable
  public data class GetAccountListsResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountListsResponse()

  @Serializable
  public data class GetAccountListsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountListsResponse()
}

public fun AccountsApiV1AccountsIdListsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdListsGetClient = DefaultAccountsApiV1AccountsIdListsGetClient(configuration)

public class DefaultAccountsApiV1AccountsIdListsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdListsGetClient {
  override suspend fun getAccountLists(id: String): AccountsApiV1AccountsIdListsGetClient.GetAccountListsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/lists".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdListsGetClient.GetAccountListsResponseSuccess(response.body<CollectionsList<ModelList>>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdListsGetClient.GetAccountListsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdListsGetClient.GetAccountListsResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdListsGetClient.GetAccountListsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdListsGetClient.GetAccountListsResponseUnknownFailure(500)
    }
  }
}
