package org.litote.mastodon.ktor.sdk.followRequestsApiV1FollowRequestsAccountIdAuthorizePost.client

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

public interface FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient {
  /**
   * Accept follow request
   */
  public suspend fun postFollowRequestAuthorize(accountId: String): PostFollowRequestAuthorizeResponse

  @Serializable
  public sealed class PostFollowRequestAuthorizeResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostFollowRequestAuthorizeResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestAuthorizeResponse() {
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
  public data class PostFollowRequestAuthorizeResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestAuthorizeResponse()

  @Serializable
  public data class PostFollowRequestAuthorizeResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestAuthorizeResponse()

  @Serializable
  public data class PostFollowRequestAuthorizeResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestAuthorizeResponse()

  @Serializable
  public data class PostFollowRequestAuthorizeResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFollowRequestAuthorizeResponse()
}

public fun FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient(configuration: ClientConfiguration = defaultClientConfiguration): FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient = DefaultFollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient(configuration)

public class DefaultFollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient {
  override suspend fun postFollowRequestAuthorize(accountId: String): FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponse {
    try {
      val response = configuration.client.post("api/v1/follow_requests/{account_id}/authorize".replace("/{account_id}", "/${accountId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 429, 503 -> FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponseFailure401(response.body<Error>(), response.headers)
        410 -> FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponseFailure410(response.headers)
        422 -> FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FollowRequestsApiV1FollowRequestsAccountIdAuthorizePostClient.PostFollowRequestAuthorizeResponseUnknownFailure(500)
    }
  }
}
