package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesPublicGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface TimelinesApiV1TimelinesPublicGetClient {
  /**
   * View public timeline
   */
  public suspend fun getTimelinePublic(
    limit: Long? = 20,
    local: Boolean? = false,
    maxId: String? = null,
    minId: String? = null,
    onlyMedia: Boolean? = false,
    remote: Boolean? = false,
    sinceId: String? = null,
  ): GetTimelinePublicResponse

  @Serializable
  public sealed class GetTimelinePublicResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetTimelinePublicResponseSuccess(
    public val body: List<Status>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinePublicResponse() {
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
  public data class GetTimelinePublicResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinePublicResponse()

  @Serializable
  public data class GetTimelinePublicResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinePublicResponse()

  @Serializable
  public data class GetTimelinePublicResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinePublicResponse()

  @Serializable
  public data class GetTimelinePublicResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinePublicResponse()
}

public fun TimelinesApiV1TimelinesPublicGetClient(configuration: ClientConfiguration = defaultClientConfiguration): TimelinesApiV1TimelinesPublicGetClient = DefaultTimelinesApiV1TimelinesPublicGetClient(configuration)

public class DefaultTimelinesApiV1TimelinesPublicGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TimelinesApiV1TimelinesPublicGetClient {
  override suspend fun getTimelinePublic(
    limit: Long?,
    local: Boolean?,
    maxId: String?,
    minId: String?,
    onlyMedia: Boolean?,
    remote: Boolean?,
    sinceId: String?,
  ): TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/public") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (local != null) {
            parameters.append("local", local.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (onlyMedia != null) {
            parameters.append("only_media", onlyMedia.toString())
          }
          if (remote != null) {
            parameters.append("remote", remote.toString())
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponseSuccess(response.body<List<Status>>(), response.headers)
        401, 404, 429, 503 -> TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponseFailure401(response.body<Error>(), response.headers)
        410 -> TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponseFailure410(response.headers)
        422 -> TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TimelinesApiV1TimelinesPublicGetClient.GetTimelinePublicResponseUnknownFailure(500)
    }
  }
}
