package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdFavouritePost.client

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

public interface StatusesApiV1StatusesIdFavouritePostClient {
  /**
   * Favourite a status
   */
  public suspend fun postStatusFavourite(id: String): PostStatusFavouriteResponse

  @Serializable
  public sealed class PostStatusFavouriteResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusFavouriteResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusFavouriteResponse() {
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
  public data class PostStatusFavouriteResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusFavouriteResponse()

  @Serializable
  public data class PostStatusFavouriteResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusFavouriteResponse()

  @Serializable
  public data class PostStatusFavouriteResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusFavouriteResponse()

  @Serializable
  public data class PostStatusFavouriteResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusFavouriteResponse()
}

public fun StatusesApiV1StatusesIdFavouritePostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdFavouritePostClient = DefaultStatusesApiV1StatusesIdFavouritePostClient(configuration)

public class DefaultStatusesApiV1StatusesIdFavouritePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdFavouritePostClient {
  override suspend fun postStatusFavourite(id: String): StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/favourite".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdFavouritePostClient.PostStatusFavouriteResponseUnknownFailure(500)
    }
  }
}
