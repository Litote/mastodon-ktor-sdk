package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdRemoveFromFollowersPost.client

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

public interface AccountsApiV1AccountsIdRemoveFromFollowersPostClient {
  /**
   * Remove account from followers
   */
  public suspend fun postAccountRemoveFromFollowers(id: String): PostAccountRemoveFromFollowersResponse

  @Serializable
  public sealed class PostAccountRemoveFromFollowersResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountRemoveFromFollowersResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountRemoveFromFollowersResponse() {
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
  public data class PostAccountRemoveFromFollowersResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountRemoveFromFollowersResponse()

  @Serializable
  public data class PostAccountRemoveFromFollowersResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountRemoveFromFollowersResponse()

  @Serializable
  public data class PostAccountRemoveFromFollowersResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountRemoveFromFollowersResponse()
}

public fun AccountsApiV1AccountsIdRemoveFromFollowersPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdRemoveFromFollowersPostClient = DefaultAccountsApiV1AccountsIdRemoveFromFollowersPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdRemoveFromFollowersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdRemoveFromFollowersPostClient {
  override suspend fun postAccountRemoveFromFollowers(id: String): AccountsApiV1AccountsIdRemoveFromFollowersPostClient.PostAccountRemoveFromFollowersResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/remove_from_followers".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdRemoveFromFollowersPostClient.PostAccountRemoveFromFollowersResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdRemoveFromFollowersPostClient.PostAccountRemoveFromFollowersResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdRemoveFromFollowersPostClient.PostAccountRemoveFromFollowersResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdRemoveFromFollowersPostClient.PostAccountRemoveFromFollowersResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdRemoveFromFollowersPostClient.PostAccountRemoveFromFollowersResponseUnknownFailure(500)
    }
  }
}
