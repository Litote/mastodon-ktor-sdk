package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.CreateStatusRequest
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.CreateStatusResponse
import io.ktor.client.request.`header` as setHeader

public interface StatusesApiV1StatusesPostClient {
  /**
   * Post a new status
   */
  public suspend fun createStatus(request: CreateStatusRequest, idempotencyKey: JsonElement? = null): CreateStatusResponse

  @Serializable
  public sealed class CreateStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateStatusResponseSuccess(
    public val body:
        org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.CreateStatusResponse,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse() {
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
  public data class CreateStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse()

  @Serializable
  public data class CreateStatusResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse()

  @Serializable
  public data class CreateStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse()
}

public fun StatusesApiV1StatusesPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesPostClient = DefaultStatusesApiV1StatusesPostClient(configuration)

public class DefaultStatusesApiV1StatusesPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesPostClient {
  override suspend fun createStatus(request: CreateStatusRequest, idempotencyKey: JsonElement?): StatusesApiV1StatusesPostClient.CreateStatusResponse {
    try {
      val response = configuration.client.post("api/v1/statuses") {
        if (idempotencyKey != null) {
          setHeader("Idempotency-Key", idempotencyKey)
        }
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesPostClient.CreateStatusResponseSuccess(response.body<CreateStatusResponse>(), response.headers)
        401, 404, 422, 429, 503 -> StatusesApiV1StatusesPostClient.CreateStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesPostClient.CreateStatusResponseFailure(response.headers)
        else -> StatusesApiV1StatusesPostClient.CreateStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesPostClient.CreateStatusResponseUnknownFailure(500)
    }
  }
}
