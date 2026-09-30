package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdEndorsePost.client

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

public interface AccountsApiV1AccountsIdEndorsePostClient {
  /**
   * Feature account on your profile
   */
  public suspend fun postAccountEndorse(id: String): PostAccountEndorseResponse

  @Serializable
  public sealed class PostAccountEndorseResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountEndorseResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountEndorseResponse() {
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
  public data class PostAccountEndorseResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountEndorseResponse()

  @Serializable
  public data class PostAccountEndorseResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountEndorseResponse()

  @Serializable
  public data class PostAccountEndorseResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountEndorseResponse()
}

public fun AccountsApiV1AccountsIdEndorsePostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdEndorsePostClient = DefaultAccountsApiV1AccountsIdEndorsePostClient(configuration)

public class DefaultAccountsApiV1AccountsIdEndorsePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdEndorsePostClient {
  override suspend fun postAccountEndorse(id: String): AccountsApiV1AccountsIdEndorsePostClient.PostAccountEndorseResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/endorse".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdEndorsePostClient.PostAccountEndorseResponseSuccess(response.body<Relationship>(), response.headers)
        401, 403, 404, 422, 429, 500, 503 -> AccountsApiV1AccountsIdEndorsePostClient.PostAccountEndorseResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdEndorsePostClient.PostAccountEndorseResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdEndorsePostClient.PostAccountEndorseResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdEndorsePostClient.PostAccountEndorseResponseUnknownFailure(500)
    }
  }
}
