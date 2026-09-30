package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdAccountsGet.client

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

public interface ListsApiV1ListsIdAccountsGetClient {
  /**
   * View accounts in a list
   */
  public suspend fun getListAccounts(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetListAccountsResponse

  @Serializable
  public sealed class GetListAccountsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetListAccountsResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListAccountsResponse() {
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
  public data class GetListAccountsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListAccountsResponse()

  @Serializable
  public data class GetListAccountsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListAccountsResponse()

  @Serializable
  public data class GetListAccountsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListAccountsResponse()

  @Serializable
  public data class GetListAccountsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListAccountsResponse()
}

public fun ListsApiV1ListsIdAccountsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsIdAccountsGetClient = DefaultListsApiV1ListsIdAccountsGetClient(configuration)

public class DefaultListsApiV1ListsIdAccountsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsIdAccountsGetClient {
  override suspend fun getListAccounts(
    id: String,
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponse {
    try {
      val response = configuration.client.`get`("api/v1/lists/{id}/accounts".replace("/{id}", "/${id.encodeURLPathPart()}")) {
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
        200 -> ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponseFailure410(response.headers)
        422 -> ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsIdAccountsGetClient.GetListAccountsResponseUnknownFailure(500)
    }
  }
}
