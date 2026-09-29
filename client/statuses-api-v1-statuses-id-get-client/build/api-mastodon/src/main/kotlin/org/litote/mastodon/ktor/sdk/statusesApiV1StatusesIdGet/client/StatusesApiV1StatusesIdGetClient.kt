package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdGetClient {
  /**
   * View a single status
   */
  public suspend fun getStatus(id: String): GetStatusResponse

  @Serializable
  public sealed class GetStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse() {
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
  public data class GetStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()
}

public fun StatusesApiV1StatusesIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdGetClient = DefaultStatusesApiV1StatusesIdGetClient(configuration)

public class DefaultStatusesApiV1StatusesIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdGetClient {
  override suspend fun getStatus(id: String): StatusesApiV1StatusesIdGetClient.GetStatusResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdGetClient.GetStatusResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdGetClient.GetStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdGetClient.GetStatusResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdGetClient.GetStatusResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdGetClient.GetStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdGetClient.GetStatusResponseUnknownFailure(500)
    }
  }
}
