package org.litote.mastodon.ktor.sdk.markersApiV1MarkersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum
import org.litote.mastodon.ktor.sdk.sharedMarkersapiv1markersgetMarkersapiv1markerspost.model.Marker

public interface MarkersApiV1MarkersGetClient {
  /**
   * Get saved timeline positions
   */
  public suspend fun getMarkers(timeline: List<FilterContextEnum>? = null): GetMarkersResponse

  @Serializable
  public sealed class GetMarkersResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetMarkersResponseSuccess(
    public val body: Map<String, Marker>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMarkersResponse() {
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
  public data class GetMarkersResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMarkersResponse()

  @Serializable
  public data class GetMarkersResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMarkersResponse()

  @Serializable
  public data class GetMarkersResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMarkersResponse()

  @Serializable
  public data class GetMarkersResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMarkersResponse()
}

public fun MarkersApiV1MarkersGetClient(configuration: ClientConfiguration = defaultClientConfiguration): MarkersApiV1MarkersGetClient = DefaultMarkersApiV1MarkersGetClient(configuration)

public class DefaultMarkersApiV1MarkersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MarkersApiV1MarkersGetClient {
  override suspend fun getMarkers(timeline: List<FilterContextEnum>?): MarkersApiV1MarkersGetClient.GetMarkersResponse {
    try {
      val response = configuration.client.`get`("api/v1/markers") {
        url {
          if (timeline != null) {
            parameters.appendAll("timeline", timeline.map { it.serialName() })
          }
        }
      }
      return when (response.status.value) {
        200 -> MarkersApiV1MarkersGetClient.GetMarkersResponseSuccess(response.body<Map<String, Marker>>(), response.headers)
        401, 404, 429, 503 -> MarkersApiV1MarkersGetClient.GetMarkersResponseFailure401(response.body<Error>(), response.headers)
        410 -> MarkersApiV1MarkersGetClient.GetMarkersResponseFailure410(response.headers)
        422 -> MarkersApiV1MarkersGetClient.GetMarkersResponseFailure(response.body<ValidationError>(), response.headers)
        else -> MarkersApiV1MarkersGetClient.GetMarkersResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MarkersApiV1MarkersGetClient.GetMarkersResponseUnknownFailure(500)
    }
  }
}
