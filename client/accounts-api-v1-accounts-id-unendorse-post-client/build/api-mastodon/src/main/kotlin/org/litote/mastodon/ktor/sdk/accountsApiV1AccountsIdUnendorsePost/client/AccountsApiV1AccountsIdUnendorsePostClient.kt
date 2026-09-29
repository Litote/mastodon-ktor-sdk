package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnendorsePost.client

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

public interface AccountsApiV1AccountsIdUnendorsePostClient {
  /**
   * Unfeature account from profile
   */
  public suspend fun postAccountUnendorse(id: String): PostAccountUnendorseResponse

  @Serializable
  public sealed class PostAccountUnendorseResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountUnendorseResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnendorseResponse() {
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
  public data class PostAccountUnendorseResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnendorseResponse()

  @Serializable
  public data class PostAccountUnendorseResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnendorseResponse()

  @Serializable
  public data class PostAccountUnendorseResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountUnendorseResponse()
}

public fun AccountsApiV1AccountsIdUnendorsePostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdUnendorsePostClient = DefaultAccountsApiV1AccountsIdUnendorsePostClient(configuration)

public class DefaultAccountsApiV1AccountsIdUnendorsePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdUnendorsePostClient {
  override suspend fun postAccountUnendorse(id: String): AccountsApiV1AccountsIdUnendorsePostClient.PostAccountUnendorseResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unendorse".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdUnendorsePostClient.PostAccountUnendorseResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdUnendorsePostClient.PostAccountUnendorseResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdUnendorsePostClient.PostAccountUnendorseResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdUnendorsePostClient.PostAccountUnendorseResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdUnendorsePostClient.PostAccountUnendorseResponseUnknownFailure(500)
    }
  }
}
