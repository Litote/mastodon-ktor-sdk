package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingHealthGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class StreamingApiV1StreamingHealthGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Check if the server is alive
   */
  public suspend fun getStreamingHealth(): GetStreamingHealthResponse {
    try {
      val response = configuration.client.`get`("api/v1/streaming/health") {
      }
      return when (response.status.value) {
        200 -> GetStreamingHealthResponseSuccess
        401, 404, 429, 503 -> GetStreamingHealthResponseFailure401(response.body<Error>())
        410 -> GetStreamingHealthResponseFailure410
        422 -> GetStreamingHealthResponseFailure(response.body<ValidationError>())
        else -> GetStreamingHealthResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStreamingHealthResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStreamingHealthResponse

  @Serializable
  public object GetStreamingHealthResponseSuccess : GetStreamingHealthResponse()

  @Serializable
  public data class GetStreamingHealthResponseFailure401(
    public val body: Error,
  ) : GetStreamingHealthResponse()

  @Serializable
  public object GetStreamingHealthResponseFailure410 : GetStreamingHealthResponse()

  @Serializable
  public data class GetStreamingHealthResponseFailure(
    public val body: ValidationError,
  ) : GetStreamingHealthResponse()

  @Serializable
  public data class GetStreamingHealthResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStreamingHealthResponse()
}
