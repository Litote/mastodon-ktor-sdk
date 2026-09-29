package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnfollowPost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public interface AccountsApiV1AccountsIdUnfollowPostClient {
  /**
   * Unfollow account
   */
  public suspend fun postAccountUnfollow(id: String): PostAccountUnfollowResponse

  @Serializable
  public sealed class PostAccountUnfollowResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountUnfollowResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnfollowResponse() {
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
  public data class PostAccountUnfollowResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnfollowResponse()

  @Serializable
  public data class PostAccountUnfollowResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnfollowResponse()

  @Serializable
  public data class PostAccountUnfollowResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnfollowResponse()
}

public fun AccountsApiV1AccountsIdUnfollowPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdUnfollowPostClient = DefaultAccountsApiV1AccountsIdUnfollowPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdUnfollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdUnfollowPostClient {
  override suspend fun postAccountUnfollow(id: String): AccountsApiV1AccountsIdUnfollowPostClient.PostAccountUnfollowResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unfollow".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdUnfollowPostClient.PostAccountUnfollowResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdUnfollowPostClient.PostAccountUnfollowResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdUnfollowPostClient.PostAccountUnfollowResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdUnfollowPostClient.PostAccountUnfollowResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdUnfollowPostClient.PostAccountUnfollowResponseUnknownFailure(500)
    }
  }
}
