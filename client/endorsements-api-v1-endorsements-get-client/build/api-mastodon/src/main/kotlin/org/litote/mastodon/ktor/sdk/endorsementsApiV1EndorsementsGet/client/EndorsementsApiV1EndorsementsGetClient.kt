package org.litote.mastodon.ktor.sdk.endorsementsApiV1EndorsementsGet.client

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

public interface EndorsementsApiV1EndorsementsGetClient {
  /**
   * View currently featured profiles
   */
  public suspend fun getEndorsements(
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetEndorsementsResponse

  @Serializable
  public sealed class GetEndorsementsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetEndorsementsResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetEndorsementsResponse() {
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
  public data class GetEndorsementsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetEndorsementsResponse()

  @Serializable
  public data class GetEndorsementsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetEndorsementsResponse()

  @Serializable
  public data class GetEndorsementsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetEndorsementsResponse()

  @Serializable
  public data class GetEndorsementsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetEndorsementsResponse()
}

public fun EndorsementsApiV1EndorsementsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): EndorsementsApiV1EndorsementsGetClient = DefaultEndorsementsApiV1EndorsementsGetClient(configuration)

public class DefaultEndorsementsApiV1EndorsementsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : EndorsementsApiV1EndorsementsGetClient {
  override suspend fun getEndorsements(
    limit: Long?,
    maxId: String?,
    sinceId: String?,
  ): EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponse {
    try {
      val response = configuration.client.`get`("api/v1/endorsements") {
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
        200 -> EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponseFailure401(response.body<Error>(), response.headers)
        410 -> EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponseFailure410(response.headers)
        422 -> EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return EndorsementsApiV1EndorsementsGetClient.GetEndorsementsResponseUnknownFailure(500)
    }
  }
}
