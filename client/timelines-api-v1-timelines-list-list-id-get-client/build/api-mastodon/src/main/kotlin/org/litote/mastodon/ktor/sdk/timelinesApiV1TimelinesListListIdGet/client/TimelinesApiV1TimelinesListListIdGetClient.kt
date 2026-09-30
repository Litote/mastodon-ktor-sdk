package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesListListIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface TimelinesApiV1TimelinesListListIdGetClient {
  /**
   * View list timeline
   */
  public suspend fun getTimelinesListByListId(
    listId: String,
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetTimelinesListByListIdResponse

  @Serializable
  public sealed class GetTimelinesListByListIdResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetTimelinesListByListIdResponseSuccess(
    public val body: List<Status>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinesListByListIdResponse() {
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
  public data class GetTimelinesListByListIdResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinesListByListIdResponse()

  @Serializable
  public data class GetTimelinesListByListIdResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinesListByListIdResponse()

  @Serializable
  public data class GetTimelinesListByListIdResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinesListByListIdResponse()

  @Serializable
  public data class GetTimelinesListByListIdResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTimelinesListByListIdResponse()
}

public fun TimelinesApiV1TimelinesListListIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): TimelinesApiV1TimelinesListListIdGetClient = DefaultTimelinesApiV1TimelinesListListIdGetClient(configuration)

public class DefaultTimelinesApiV1TimelinesListListIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TimelinesApiV1TimelinesListListIdGetClient {
  override suspend fun getTimelinesListByListId(
    listId: String,
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/list/{list_id}".replace("/{list_id}", "/${listId.encodeURLPathPart()}")) {
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
        200 -> TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponseSuccess(response.body<List<Status>>(), response.headers)
        401, 404, 429, 503 -> TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponseFailure401(response.body<Error>(), response.headers)
        410 -> TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponseFailure410(response.headers)
        422 -> TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TimelinesApiV1TimelinesListListIdGetClient.GetTimelinesListByListIdResponseUnknownFailure(500)
    }
  }
}
