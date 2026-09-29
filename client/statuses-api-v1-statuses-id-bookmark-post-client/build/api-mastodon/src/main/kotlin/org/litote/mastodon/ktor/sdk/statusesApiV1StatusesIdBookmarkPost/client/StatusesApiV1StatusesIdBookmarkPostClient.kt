package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdBookmarkPost.client

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

public interface StatusesApiV1StatusesIdBookmarkPostClient {
  /**
   * Bookmark a status
   */
  public suspend fun postStatusBookmark(id: String): PostStatusBookmarkResponse

  @Serializable
  public sealed class PostStatusBookmarkResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusBookmarkResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusBookmarkResponse() {
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
  public data class PostStatusBookmarkResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusBookmarkResponse()

  @Serializable
  public data class PostStatusBookmarkResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusBookmarkResponse()

  @Serializable
  public data class PostStatusBookmarkResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusBookmarkResponse()

  @Serializable
  public data class PostStatusBookmarkResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusBookmarkResponse()
}

public fun StatusesApiV1StatusesIdBookmarkPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdBookmarkPostClient = DefaultStatusesApiV1StatusesIdBookmarkPostClient(configuration)

public class DefaultStatusesApiV1StatusesIdBookmarkPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdBookmarkPostClient {
  override suspend fun postStatusBookmark(id: String): StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/bookmark".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdBookmarkPostClient.PostStatusBookmarkResponseUnknownFailure(500)
    }
  }
}
