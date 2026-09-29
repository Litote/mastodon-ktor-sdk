package org.litote.mastodon.ktor.sdk.oauthOauthUserinfoGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface OauthOauthUserinfoGetClient {
  /**
   * Retrieve user information
   */
  public suspend fun getOauthUserinfo(): GetOauthUserinfoResponse

  @Serializable
  public sealed class GetOauthUserinfoResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetOauthUserinfoResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthUserinfoResponse() {
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
  public data class GetOauthUserinfoResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthUserinfoResponse()

  @Serializable
  public data class GetOauthUserinfoResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthUserinfoResponse()

  @Serializable
  public data class GetOauthUserinfoResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthUserinfoResponse()

  @Serializable
  public data class GetOauthUserinfoResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthUserinfoResponse()
}

public fun OauthOauthUserinfoGetClient(configuration: ClientConfiguration = defaultClientConfiguration): OauthOauthUserinfoGetClient = DefaultOauthOauthUserinfoGetClient(configuration)

public class DefaultOauthOauthUserinfoGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : OauthOauthUserinfoGetClient {
  override suspend fun getOauthUserinfo(): OauthOauthUserinfoGetClient.GetOauthUserinfoResponse {
    try {
      val response = configuration.client.`get`("oauth/userinfo") {
      }
      return when (response.status.value) {
        200 -> OauthOauthUserinfoGetClient.GetOauthUserinfoResponseSuccess(response.headers)
        401, 403, 404, 429, 503 -> OauthOauthUserinfoGetClient.GetOauthUserinfoResponseFailure401(response.body<Error>(), response.headers)
        410 -> OauthOauthUserinfoGetClient.GetOauthUserinfoResponseFailure410(response.headers)
        422 -> OauthOauthUserinfoGetClient.GetOauthUserinfoResponseFailure(response.body<ValidationError>(), response.headers)
        else -> OauthOauthUserinfoGetClient.GetOauthUserinfoResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return OauthOauthUserinfoGetClient.GetOauthUserinfoResponseUnknownFailure(500)
    }
  }
}
