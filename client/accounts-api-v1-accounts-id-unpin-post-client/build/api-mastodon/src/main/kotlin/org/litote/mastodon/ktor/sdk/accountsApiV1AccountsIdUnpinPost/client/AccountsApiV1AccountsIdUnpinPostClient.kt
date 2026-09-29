package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnpinPost.client

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

public interface AccountsApiV1AccountsIdUnpinPostClient {
  /**
   * Unfeature account from profile
   */
  public suspend fun postAccountUnpin(id: String): PostAccountUnpinResponse

  @Serializable
  public sealed class PostAccountUnpinResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountUnpinResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnpinResponse() {
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
  public data class PostAccountUnpinResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnpinResponse()

  @Serializable
  public data class PostAccountUnpinResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnpinResponse()

  @Serializable
  public data class PostAccountUnpinResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnpinResponse()
}

public fun AccountsApiV1AccountsIdUnpinPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdUnpinPostClient = DefaultAccountsApiV1AccountsIdUnpinPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdUnpinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdUnpinPostClient {
  override suspend fun postAccountUnpin(id: String): AccountsApiV1AccountsIdUnpinPostClient.PostAccountUnpinResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unpin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdUnpinPostClient.PostAccountUnpinResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdUnpinPostClient.PostAccountUnpinResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdUnpinPostClient.PostAccountUnpinResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdUnpinPostClient.PostAccountUnpinResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdUnpinPostClient.PostAccountUnpinResponseUnknownFailure(500)
    }
  }
}
