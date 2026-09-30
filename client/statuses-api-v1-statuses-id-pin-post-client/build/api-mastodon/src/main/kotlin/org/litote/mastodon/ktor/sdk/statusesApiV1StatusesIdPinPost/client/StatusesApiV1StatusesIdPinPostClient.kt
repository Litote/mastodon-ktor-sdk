package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdPinPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdPinPostClient {
  /**
   * Pin status to profile
   */
  public suspend fun postStatusPin(id: String): PostStatusPinResponse

  @Serializable
  public sealed class PostStatusPinResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusPinResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusPinResponse() {
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
  public data class PostStatusPinResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusPinResponse()

  @Serializable
  public data class PostStatusPinResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusPinResponse()

  @Serializable
  public data class PostStatusPinResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusPinResponse()
}

public fun StatusesApiV1StatusesIdPinPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdPinPostClient = DefaultStatusesApiV1StatusesIdPinPostClient(configuration)

public class DefaultStatusesApiV1StatusesIdPinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdPinPostClient {
  override suspend fun postStatusPin(id: String): StatusesApiV1StatusesIdPinPostClient.PostStatusPinResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/pin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdPinPostClient.PostStatusPinResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 422, 429, 503 -> StatusesApiV1StatusesIdPinPostClient.PostStatusPinResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdPinPostClient.PostStatusPinResponseFailure(response.headers)
        else -> StatusesApiV1StatusesIdPinPostClient.PostStatusPinResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdPinPostClient.PostStatusPinResponseUnknownFailure(500)
    }
  }
}
