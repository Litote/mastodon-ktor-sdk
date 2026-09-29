package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdFavouritedByGet.client

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

public interface StatusesApiV1StatusesIdFavouritedByGetClient {
  /**
   * See who favourited a status
   */
  public suspend fun getStatusFavouritedBy(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetStatusFavouritedByResponse

  @Serializable
  public sealed class GetStatusFavouritedByResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusFavouritedByResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusFavouritedByResponse() {
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
  public data class GetStatusFavouritedByResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusFavouritedByResponse()

  @Serializable
  public data class GetStatusFavouritedByResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusFavouritedByResponse()

  @Serializable
  public data class GetStatusFavouritedByResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusFavouritedByResponse()

  @Serializable
  public data class GetStatusFavouritedByResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusFavouritedByResponse()
}

public fun StatusesApiV1StatusesIdFavouritedByGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdFavouritedByGetClient = DefaultStatusesApiV1StatusesIdFavouritedByGetClient(configuration)

public class DefaultStatusesApiV1StatusesIdFavouritedByGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdFavouritedByGetClient {
  override suspend fun getStatusFavouritedBy(
    id: String,
    limit: Long?,
    maxId: String?,
    sinceId: String?,
  ): StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/favourited_by".replace("/{id}", "/${id.encodeURLPathPart()}")) {
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
        200 -> StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdFavouritedByGetClient.GetStatusFavouritedByResponseUnknownFailure(500)
    }
  }
}
