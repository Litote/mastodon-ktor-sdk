package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnbookmarkPost.client

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

public interface StatusesApiV1StatusesIdUnbookmarkPostClient {
  /**
   * Undo bookmark of a status
   */
  public suspend fun postStatusUnbookmark(id: String): PostStatusUnbookmarkResponse

  @Serializable
  public sealed class PostStatusUnbookmarkResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusUnbookmarkResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnbookmarkResponse() {
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
  public data class PostStatusUnbookmarkResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnbookmarkResponse()

  @Serializable
  public data class PostStatusUnbookmarkResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnbookmarkResponse()

  @Serializable
  public data class PostStatusUnbookmarkResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnbookmarkResponse()

  @Serializable
  public data class PostStatusUnbookmarkResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnbookmarkResponse()
}

public fun StatusesApiV1StatusesIdUnbookmarkPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdUnbookmarkPostClient = DefaultStatusesApiV1StatusesIdUnbookmarkPostClient(configuration)

public class DefaultStatusesApiV1StatusesIdUnbookmarkPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdUnbookmarkPostClient {
  override suspend fun postStatusUnbookmark(id: String): StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unbookmark".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdUnbookmarkPostClient.PostStatusUnbookmarkResponseUnknownFailure(500)
    }
  }
}
