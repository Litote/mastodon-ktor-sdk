package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnblockPost.client

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

public interface AccountsApiV1AccountsIdUnblockPostClient {
  /**
   * Unblock account
   */
  public suspend fun postAccountUnblock(id: String): PostAccountUnblockResponse

  @Serializable
  public sealed class PostAccountUnblockResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountUnblockResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnblockResponse() {
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
  public data class PostAccountUnblockResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnblockResponse()

  @Serializable
  public data class PostAccountUnblockResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnblockResponse()

  @Serializable
  public data class PostAccountUnblockResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnblockResponse()
}

public fun AccountsApiV1AccountsIdUnblockPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdUnblockPostClient = DefaultAccountsApiV1AccountsIdUnblockPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdUnblockPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdUnblockPostClient {
  override suspend fun postAccountUnblock(id: String): AccountsApiV1AccountsIdUnblockPostClient.PostAccountUnblockResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unblock".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdUnblockPostClient.PostAccountUnblockResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdUnblockPostClient.PostAccountUnblockResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdUnblockPostClient.PostAccountUnblockResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdUnblockPostClient.PostAccountUnblockResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdUnblockPostClient.PostAccountUnblockResponseUnknownFailure(500)
    }
  }
}
