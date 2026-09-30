package org.litote.mastodon.ktor.sdk.healthHealthGet.client

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

public interface HealthHealthGetClient {
  /**
   * Get basic health status as JSON
   */
  public suspend fun getHealth(): GetHealthResponse

  @Serializable
  public sealed class GetHealthResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetHealthResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetHealthResponse() {
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
  public data class GetHealthResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetHealthResponse()

  @Serializable
  public data class GetHealthResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetHealthResponse()

  @Serializable
  public data class GetHealthResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetHealthResponse()

  @Serializable
  public data class GetHealthResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetHealthResponse()
}

public fun HealthHealthGetClient(configuration: ClientConfiguration = defaultClientConfiguration): HealthHealthGetClient = DefaultHealthHealthGetClient(configuration)

public class DefaultHealthHealthGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : HealthHealthGetClient {
  override suspend fun getHealth(): HealthHealthGetClient.GetHealthResponse {
    try {
      val response = configuration.client.`get`("health") {
      }
      return when (response.status.value) {
        200 -> HealthHealthGetClient.GetHealthResponseSuccess(response.headers)
        401, 404, 429, 503 -> HealthHealthGetClient.GetHealthResponseFailure401(response.body<Error>(), response.headers)
        410 -> HealthHealthGetClient.GetHealthResponseFailure410(response.headers)
        422 -> HealthHealthGetClient.GetHealthResponseFailure(response.body<ValidationError>(), response.headers)
        else -> HealthHealthGetClient.GetHealthResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return HealthHealthGetClient.GetHealthResponseUnknownFailure(500)
    }
  }
}
