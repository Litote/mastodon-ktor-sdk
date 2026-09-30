package org.litote.mastodon.ktor.sdk.followRequestsApiV1FollowRequestsAccountIdRejectPost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public interface FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient {
  /**
   * Reject follow request
   */
  public suspend fun postFollowRequestReject(accountId: String): PostFollowRequestRejectResponse

  @Serializable
  public sealed class PostFollowRequestRejectResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostFollowRequestRejectResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestRejectResponse() {
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
  public data class PostFollowRequestRejectResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestRejectResponse()

  @Serializable
  public data class PostFollowRequestRejectResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestRejectResponse()

  @Serializable
  public data class PostFollowRequestRejectResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestRejectResponse()

  @Serializable
  public data class PostFollowRequestRejectResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestRejectResponse()
}

public fun FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient(configuration: ClientConfiguration = defaultClientConfiguration): FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient = DefaultFollowRequestsApiV1FollowRequestsAccountIdRejectPostClient(configuration)

public class DefaultFollowRequestsApiV1FollowRequestsAccountIdRejectPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient {
  override suspend fun postFollowRequestReject(accountId: String): FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponse {
    try {
      val response = configuration.client.post("api/v1/follow_requests/{account_id}/reject".replace("/{account_id}", "/${accountId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 429, 503 -> FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponseFailure401(response.body<Error>(), response.headers)
        410 -> FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponseFailure410(response.headers)
        422 -> FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FollowRequestsApiV1FollowRequestsAccountIdRejectPostClient.PostFollowRequestRejectResponseUnknownFailure(500)
    }
  }
}
