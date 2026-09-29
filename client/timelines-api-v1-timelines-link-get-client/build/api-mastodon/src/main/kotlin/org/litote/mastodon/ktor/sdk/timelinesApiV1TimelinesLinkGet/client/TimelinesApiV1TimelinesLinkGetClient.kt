package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesLinkGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface TimelinesApiV1TimelinesLinkGetClient {
  /**
   * View link timeline
   */
  public suspend fun getTimelineLink(
    url: String,
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetTimelineLinkResponse

  @Serializable
  public sealed class GetTimelineLinkResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetTimelineLinkResponseSuccess(
    public val body: List<Status>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelineLinkResponse() {
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
  public data class GetTimelineLinkResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelineLinkResponse()

  @Serializable
  public data class GetTimelineLinkResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelineLinkResponse()

  @Serializable
  public data class GetTimelineLinkResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelineLinkResponse()

  @Serializable
  public data class GetTimelineLinkResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelineLinkResponse()
}

public fun TimelinesApiV1TimelinesLinkGetClient(configuration: ClientConfiguration = defaultClientConfiguration): TimelinesApiV1TimelinesLinkGetClient = DefaultTimelinesApiV1TimelinesLinkGetClient(configuration)

public class DefaultTimelinesApiV1TimelinesLinkGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TimelinesApiV1TimelinesLinkGetClient {
  override suspend fun getTimelineLink(
    url: String,
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/link") {
        url {
          parameters.append("url", url)
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
        200 -> TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponseSuccess(response.body<List<Status>>(), response.headers)
        401, 404, 429, 503 -> TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponseFailure401(response.body<Error>(), response.headers)
        410 -> TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponseFailure410(response.headers)
        422 -> TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TimelinesApiV1TimelinesLinkGetClient.GetTimelineLinkResponseUnknownFailure(500)
    }
  }
}
