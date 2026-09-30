package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingHealthGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface StreamingApiV1StreamingHealthGetClient {
  /**
   * Check if the server is alive
   */
  public suspend fun getStreamingHealth(): GetStreamingHealthResponse

  @Serializable
  public sealed class GetStreamingHealthResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStreamingHealthResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStreamingHealthResponse() {
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
  public data class GetStreamingHealthResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStreamingHealthResponse()

  @Serializable
  public data class GetStreamingHealthResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStreamingHealthResponse()

  @Serializable
  public data class GetStreamingHealthResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStreamingHealthResponse()

  @Serializable
  public data class GetStreamingHealthResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStreamingHealthResponse()
}

public fun StreamingApiV1StreamingHealthGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingHealthGetClient = DefaultStreamingApiV1StreamingHealthGetClient(configuration)

public class DefaultStreamingApiV1StreamingHealthGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingHealthGetClient {
  override suspend fun getStreamingHealth(): StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponse {
    try {
      val response = configuration.client.`get`("api/v1/streaming/health") {
      }
      return when (response.status.value) {
        200 -> StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponseSuccess(response.headers)
        401, 404, 429, 503 -> StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponseFailure401(response.body<Error>(), response.headers)
        410 -> StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponseFailure410(response.headers)
        422 -> StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StreamingApiV1StreamingHealthGetClient.GetStreamingHealthResponseUnknownFailure(500)
    }
  }
}
