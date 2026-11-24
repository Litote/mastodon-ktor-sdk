package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdMutePost.client

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

public interface StatusesApiV1StatusesIdMutePostClient {
  /**
   * Mute a conversation
   */
  public suspend fun postStatusMute(id: String): PostStatusMuteResponse

  @Serializable
  public sealed class PostStatusMuteResponse : OperationResponse<Status> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class PostStatusMuteResponseSuccess(
    override val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusMuteResponse(),
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
  public data class PostStatusMuteResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusMuteResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class PostStatusMuteResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusMuteResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class PostStatusMuteResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusMuteResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class PostStatusMuteResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusMuteResponse(),
      OperationResponseUnknownFailure
}

public fun StatusesApiV1StatusesIdMutePostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdMutePostClient = DefaultStatusesApiV1StatusesIdMutePostClient(configuration)

public class DefaultStatusesApiV1StatusesIdMutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdMutePostClient {
  override suspend fun postStatusMute(id: String): StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/mute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdMutePostClient.PostStatusMuteResponseUnknownFailure(500)
    }
  }
}
