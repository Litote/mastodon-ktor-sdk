package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnmutePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.client.OperationResponse
import org.litote.mastodon.ktor.sdk.client.OperationResponseFailure
import org.litote.mastodon.ktor.sdk.client.OperationResponseSuccess
import org.litote.mastodon.ktor.sdk.client.OperationResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdUnmutePostClient {
  /**
   * Unmute a conversation
   */
  public suspend fun postStatusUnmute(id: String): PostStatusUnmuteResponse

  @Serializable
  public sealed class PostStatusUnmuteResponse : OperationResponse<Status> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class PostStatusUnmuteResponseSuccess(
    override val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnmuteResponse(),
      OperationResponseSuccess<Status> {
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
  public data class PostStatusUnmuteResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnmuteResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class PostStatusUnmuteResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnmuteResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class PostStatusUnmuteResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnmuteResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class PostStatusUnmuteResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusUnmuteResponse(),
      OperationResponseUnknownFailure
}

public fun StatusesApiV1StatusesIdUnmutePostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdUnmutePostClient = DefaultStatusesApiV1StatusesIdUnmutePostClient(configuration)

public class DefaultStatusesApiV1StatusesIdUnmutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdUnmutePostClient {
  override suspend fun postStatusUnmute(id: String): StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unmute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdUnmutePostClient.PostStatusUnmuteResponseUnknownFailure(500)
    }
  }
}
