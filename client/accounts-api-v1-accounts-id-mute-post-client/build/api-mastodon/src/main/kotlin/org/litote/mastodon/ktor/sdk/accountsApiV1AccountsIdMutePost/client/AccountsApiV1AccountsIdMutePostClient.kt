package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdMutePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public interface AccountsApiV1AccountsIdMutePostClient {
  /**
   * Mute account
   */
  public suspend fun postAccountMute(request: PostAccountMuteRequest, id: String): PostAccountMuteResponse

  @Serializable
  public data class PostAccountMuteRequest(
    public val duration: Long? = 0,
    public val notifications: Boolean? = true,
  )

  @Serializable
  public sealed class PostAccountMuteResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountMuteResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountMuteResponse() {
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
  public data class PostAccountMuteResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountMuteResponse()

  @Serializable
  public data class PostAccountMuteResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountMuteResponse()

  @Serializable
  public data class PostAccountMuteResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountMuteResponse()
}

public fun AccountsApiV1AccountsIdMutePostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdMutePostClient = DefaultAccountsApiV1AccountsIdMutePostClient(configuration)

public class DefaultAccountsApiV1AccountsIdMutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdMutePostClient {
  override suspend fun postAccountMute(request: AccountsApiV1AccountsIdMutePostClient.PostAccountMuteRequest, id: String): AccountsApiV1AccountsIdMutePostClient.PostAccountMuteResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/mute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdMutePostClient.PostAccountMuteResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdMutePostClient.PostAccountMuteResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdMutePostClient.PostAccountMuteResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdMutePostClient.PostAccountMuteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdMutePostClient.PostAccountMuteResponseUnknownFailure(500)
    }
  }
}
