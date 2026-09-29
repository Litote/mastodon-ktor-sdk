package org.litote.mastodon.ktor.sdk.markersApiV1MarkersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.collections.Map
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedMarkersapiv1markersgetMarkersapiv1markerspost.model.Marker

public interface MarkersApiV1MarkersPostClient {
  /**
   * Save your position in a timeline
   */
  public suspend fun createMarker(request: CreateMarkerRequest): CreateMarkerResponse

  @Serializable
  public data class CreateMarkerRequest(
    public val home: Home? = null,
    public val notifications: Notifications? = null,
  ) {
    @Serializable
    public data class Home(
      @SerialName("last_read_id")
      public val lastReadId: String? = null,
    )

    @Serializable
    public data class Notifications(
      @SerialName("last_read_id")
      public val lastReadId: String? = null,
    )
  }

  @Serializable
  public sealed class CreateMarkerResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateMarkerResponseSuccess(
    public val body: Map<String, Marker>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMarkerResponse() {
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
  public data class CreateMarkerResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMarkerResponse()

  @Serializable
  public data class CreateMarkerResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMarkerResponse()

  @Serializable
  public data class CreateMarkerResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMarkerResponse()

  @Serializable
  public data class CreateMarkerResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMarkerResponse()
}

public fun MarkersApiV1MarkersPostClient(configuration: ClientConfiguration = defaultClientConfiguration): MarkersApiV1MarkersPostClient = DefaultMarkersApiV1MarkersPostClient(configuration)

public class DefaultMarkersApiV1MarkersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MarkersApiV1MarkersPostClient {
  override suspend fun createMarker(request: MarkersApiV1MarkersPostClient.CreateMarkerRequest): MarkersApiV1MarkersPostClient.CreateMarkerResponse {
    try {
      val response = configuration.client.post("api/v1/markers") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> MarkersApiV1MarkersPostClient.CreateMarkerResponseSuccess(response.body<Map<String, Marker>>(), response.headers)
        401, 404, 429, 503 -> MarkersApiV1MarkersPostClient.CreateMarkerResponseFailure401(response.body<Error>(), response.headers)
        410 -> MarkersApiV1MarkersPostClient.CreateMarkerResponseFailure410(response.headers)
        422 -> MarkersApiV1MarkersPostClient.CreateMarkerResponseFailure(response.body<ValidationError>(), response.headers)
        else -> MarkersApiV1MarkersPostClient.CreateMarkerResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MarkersApiV1MarkersPostClient.CreateMarkerResponseUnknownFailure(500)
    }
  }
}
