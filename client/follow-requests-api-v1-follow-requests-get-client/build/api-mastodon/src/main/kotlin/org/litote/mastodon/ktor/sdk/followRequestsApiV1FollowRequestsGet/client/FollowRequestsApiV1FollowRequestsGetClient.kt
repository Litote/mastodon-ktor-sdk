package org.litote.mastodon.ktor.sdk.followRequestsApiV1FollowRequestsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
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

public interface FollowRequestsApiV1FollowRequestsGetClient {
  /**
   * View pending follow requests
   */
  public suspend fun getFollowRequests(
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetFollowRequestsResponse

  @Serializable
  public sealed class GetFollowRequestsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFollowRequestsResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowRequestsResponse() {
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
  public data class GetFollowRequestsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowRequestsResponse()

  @Serializable
  public data class GetFollowRequestsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowRequestsResponse()

  @Serializable
  public data class GetFollowRequestsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowRequestsResponse()

  @Serializable
  public data class GetFollowRequestsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowRequestsResponse()
}

public fun FollowRequestsApiV1FollowRequestsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FollowRequestsApiV1FollowRequestsGetClient = DefaultFollowRequestsApiV1FollowRequestsGetClient(configuration)

public class DefaultFollowRequestsApiV1FollowRequestsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FollowRequestsApiV1FollowRequestsGetClient {
  override suspend fun getFollowRequests(
    limit: Long?,
    maxId: String?,
    sinceId: String?,
  ): FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponse {
    try {
      val response = configuration.client.`get`("api/v1/follow_requests") {
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
        200 -> FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponseFailure401(response.body<Error>(), response.headers)
        410 -> FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponseFailure410(response.headers)
        422 -> FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FollowRequestsApiV1FollowRequestsGetClient.GetFollowRequestsResponseUnknownFailure(500)
    }
  }
}
