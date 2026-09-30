package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdPinPost.client

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

public interface AccountsApiV1AccountsIdPinPostClient {
  /**
   * Feature account on your profile
   */
  public suspend fun postAccountPin(id: String): PostAccountPinResponse

  @Serializable
  public sealed class PostAccountPinResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountPinResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountPinResponse() {
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
  public data class PostAccountPinResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountPinResponse()

  @Serializable
  public data class PostAccountPinResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountPinResponse()

  @Serializable
  public data class PostAccountPinResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountPinResponse()
}

public fun AccountsApiV1AccountsIdPinPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdPinPostClient = DefaultAccountsApiV1AccountsIdPinPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdPinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdPinPostClient {
  override suspend fun postAccountPin(id: String): AccountsApiV1AccountsIdPinPostClient.PostAccountPinResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/pin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdPinPostClient.PostAccountPinResponseSuccess(response.body<Relationship>(), response.headers)
        401, 403, 404, 422, 429, 500, 503 -> AccountsApiV1AccountsIdPinPostClient.PostAccountPinResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdPinPostClient.PostAccountPinResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdPinPostClient.PostAccountPinResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdPinPostClient.PostAccountPinResponseUnknownFailure(500)
    }
  }
}
