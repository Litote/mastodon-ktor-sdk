package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnfavouritePost.client

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

public interface StatusesApiV1StatusesIdUnfavouritePostClient {
  /**
   * Undo favourite of a status
   */
  public suspend fun postStatusUnfavourite(id: String): PostStatusUnfavouriteResponse

  @Serializable
  public sealed class PostStatusUnfavouriteResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusUnfavouriteResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnfavouriteResponse() {
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
  public data class PostStatusUnfavouriteResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnfavouriteResponse()

  @Serializable
  public data class PostStatusUnfavouriteResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnfavouriteResponse()

  @Serializable
  public data class PostStatusUnfavouriteResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnfavouriteResponse()

  @Serializable
  public data class PostStatusUnfavouriteResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnfavouriteResponse()
}

public fun StatusesApiV1StatusesIdUnfavouritePostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdUnfavouritePostClient = DefaultStatusesApiV1StatusesIdUnfavouritePostClient(configuration)

public class DefaultStatusesApiV1StatusesIdUnfavouritePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdUnfavouritePostClient {
  override suspend fun postStatusUnfavourite(id: String): StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unfavourite".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdUnfavouritePostClient.PostStatusUnfavouriteResponseUnknownFailure(500)
    }
  }
}
