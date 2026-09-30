package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdRebloggedByGet.client

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

public interface StatusesApiV1StatusesIdRebloggedByGetClient {
  /**
   * See who boosted a status
   */
  public suspend fun getStatusRebloggedBy(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetStatusRebloggedByResponse

  @Serializable
  public sealed class GetStatusRebloggedByResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusRebloggedByResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusRebloggedByResponse() {
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
  public data class GetStatusRebloggedByResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusRebloggedByResponse()

  @Serializable
  public data class GetStatusRebloggedByResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusRebloggedByResponse()

  @Serializable
  public data class GetStatusRebloggedByResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusRebloggedByResponse()

  @Serializable
  public data class GetStatusRebloggedByResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusRebloggedByResponse()
}

public fun StatusesApiV1StatusesIdRebloggedByGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdRebloggedByGetClient = DefaultStatusesApiV1StatusesIdRebloggedByGetClient(configuration)

public class DefaultStatusesApiV1StatusesIdRebloggedByGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdRebloggedByGetClient {
  override suspend fun getStatusRebloggedBy(
    id: String,
    limit: Long?,
    maxId: String?,
    sinceId: String?,
  ): StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/reblogged_by".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdRebloggedByGetClient.GetStatusRebloggedByResponseUnknownFailure(500)
    }
  }
}
