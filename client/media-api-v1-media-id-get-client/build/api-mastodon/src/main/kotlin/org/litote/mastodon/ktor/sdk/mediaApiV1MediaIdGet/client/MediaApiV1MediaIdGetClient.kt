package org.litote.mastodon.ktor.sdk.mediaApiV1MediaIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public interface MediaApiV1MediaIdGetClient {
  /**
   * Get media attachment
   */
  public suspend fun getMedia(id: String): GetMediaResponse

  @Serializable
  public sealed class GetMediaResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetMediaResponseSuccess200(
    public val body: MediaAttachment,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMediaResponse() {
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
  public data class GetMediaResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMediaResponse() {
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
  public data class GetMediaResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMediaResponse()

  @Serializable
  public data class GetMediaResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMediaResponse()

  @Serializable
  public data class GetMediaResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetMediaResponse()
}

public fun MediaApiV1MediaIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): MediaApiV1MediaIdGetClient = DefaultMediaApiV1MediaIdGetClient(configuration)

public class DefaultMediaApiV1MediaIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MediaApiV1MediaIdGetClient {
  override suspend fun getMedia(id: String): MediaApiV1MediaIdGetClient.GetMediaResponse {
    try {
      val response = configuration.client.`get`("api/v1/media/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> MediaApiV1MediaIdGetClient.GetMediaResponseSuccess200(response.body<MediaAttachment>(), response.headers)
        206 -> MediaApiV1MediaIdGetClient.GetMediaResponseSuccess(response.headers)
        401, 404, 422, 429, 503 -> MediaApiV1MediaIdGetClient.GetMediaResponseFailure401(response.body<Error>(), response.headers)
        410 -> MediaApiV1MediaIdGetClient.GetMediaResponseFailure(response.headers)
        else -> MediaApiV1MediaIdGetClient.GetMediaResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MediaApiV1MediaIdGetClient.GetMediaResponseUnknownFailure(500)
    }
  }
}
