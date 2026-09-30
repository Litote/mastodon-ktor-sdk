package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnpinPost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdUnpinPostClient {
  /**
   * Unpin status from profile
   */
  public suspend fun postStatusUnpin(id: String): PostStatusUnpinResponse

  @Serializable
  public sealed class PostStatusUnpinResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusUnpinResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnpinResponse() {
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
  public data class PostStatusUnpinResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnpinResponse()

  @Serializable
  public data class PostStatusUnpinResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnpinResponse()

  @Serializable
  public data class PostStatusUnpinResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnpinResponse()

  @Serializable
  public data class PostStatusUnpinResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnpinResponse()
}

public fun StatusesApiV1StatusesIdUnpinPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdUnpinPostClient = DefaultStatusesApiV1StatusesIdUnpinPostClient(configuration)

public class DefaultStatusesApiV1StatusesIdUnpinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdUnpinPostClient {
  override suspend fun postStatusUnpin(id: String): StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unpin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdUnpinPostClient.PostStatusUnpinResponseUnknownFailure(500)
    }
  }
}
