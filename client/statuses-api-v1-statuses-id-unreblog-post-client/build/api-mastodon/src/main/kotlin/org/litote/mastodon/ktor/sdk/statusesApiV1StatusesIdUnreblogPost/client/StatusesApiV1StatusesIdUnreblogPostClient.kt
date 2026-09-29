package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnreblogPost.client

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

public interface StatusesApiV1StatusesIdUnreblogPostClient {
  /**
   * Undo boost of a status
   */
  public suspend fun postStatusUnreblog(id: String): PostStatusUnreblogResponse

  @Serializable
  public sealed class PostStatusUnreblogResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusUnreblogResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnreblogResponse() {
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
  public data class PostStatusUnreblogResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnreblogResponse()

  @Serializable
  public data class PostStatusUnreblogResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnreblogResponse()

  @Serializable
  public data class PostStatusUnreblogResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnreblogResponse()

  @Serializable
  public data class PostStatusUnreblogResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnreblogResponse()
}

public fun StatusesApiV1StatusesIdUnreblogPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdUnreblogPostClient = DefaultStatusesApiV1StatusesIdUnreblogPostClient(configuration)

public class DefaultStatusesApiV1StatusesIdUnreblogPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdUnreblogPostClient {
  override suspend fun postStatusUnreblog(id: String): StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unreblog".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdUnreblogPostClient.PostStatusUnreblogResponseUnknownFailure(500)
    }
  }
}
