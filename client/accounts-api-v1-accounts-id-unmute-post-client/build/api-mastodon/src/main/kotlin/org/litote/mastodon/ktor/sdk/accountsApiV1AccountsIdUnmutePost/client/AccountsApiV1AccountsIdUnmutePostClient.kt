package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnmutePost.client

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

public interface AccountsApiV1AccountsIdUnmutePostClient {
  /**
   * Unmute account
   */
  public suspend fun postAccountUnmute(id: String): PostAccountUnmuteResponse

  @Serializable
  public sealed class PostAccountUnmuteResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountUnmuteResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnmuteResponse() {
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
  public data class PostAccountUnmuteResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnmuteResponse()

  @Serializable
  public data class PostAccountUnmuteResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnmuteResponse()

  @Serializable
  public data class PostAccountUnmuteResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnmuteResponse()
}

public fun AccountsApiV1AccountsIdUnmutePostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdUnmutePostClient = DefaultAccountsApiV1AccountsIdUnmutePostClient(configuration)

public class DefaultAccountsApiV1AccountsIdUnmutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdUnmutePostClient {
  override suspend fun postAccountUnmute(id: String): AccountsApiV1AccountsIdUnmutePostClient.PostAccountUnmuteResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unmute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdUnmutePostClient.PostAccountUnmuteResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdUnmutePostClient.PostAccountUnmuteResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdUnmutePostClient.PostAccountUnmuteResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdUnmutePostClient.PostAccountUnmuteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdUnmutePostClient.PostAccountUnmuteResponseUnknownFailure(500)
    }
  }
}
