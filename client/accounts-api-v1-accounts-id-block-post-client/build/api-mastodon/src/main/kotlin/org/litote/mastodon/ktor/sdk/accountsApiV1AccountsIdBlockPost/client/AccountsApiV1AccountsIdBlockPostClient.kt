package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdBlockPost.client

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

public interface AccountsApiV1AccountsIdBlockPostClient {
  /**
   * Block account
   */
  public suspend fun postAccountBlock(id: String): PostAccountBlockResponse

  @Serializable
  public sealed class PostAccountBlockResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountBlockResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountBlockResponse() {
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
  public data class PostAccountBlockResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountBlockResponse()

  @Serializable
  public data class PostAccountBlockResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountBlockResponse()

  @Serializable
  public data class PostAccountBlockResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountBlockResponse()
}

public fun AccountsApiV1AccountsIdBlockPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdBlockPostClient = DefaultAccountsApiV1AccountsIdBlockPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdBlockPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdBlockPostClient {
  override suspend fun postAccountBlock(id: String): AccountsApiV1AccountsIdBlockPostClient.PostAccountBlockResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/block".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdBlockPostClient.PostAccountBlockResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdBlockPostClient.PostAccountBlockResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdBlockPostClient.PostAccountBlockResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdBlockPostClient.PostAccountBlockResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdBlockPostClient.PostAccountBlockResponseUnknownFailure(500)
    }
  }
}
