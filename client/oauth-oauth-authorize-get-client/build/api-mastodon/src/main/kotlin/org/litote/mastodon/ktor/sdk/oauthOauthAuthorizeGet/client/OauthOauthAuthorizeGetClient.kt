package org.litote.mastodon.ktor.sdk.oauthOauthAuthorizeGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface OauthOauthAuthorizeGetClient {
  /**
   * Authorize a user
   */
  public suspend fun getOauthAuthorize(
    clientId: String,
    redirectUri: String,
    responseType: String,
    codeChallenge: String? = null,
    codeChallengeMethod: String? = null,
    forceLogin: Boolean? = null,
    lang: String? = null,
    scope: String? = "read",
    state: String? = null,
  ): GetOauthAuthorizeResponse

  @Serializable
  public sealed class GetOauthAuthorizeResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetOauthAuthorizeResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthAuthorizeResponse() {
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
  public data class GetOauthAuthorizeResponseFailure400(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthAuthorizeResponse()

  @Serializable
  public data class GetOauthAuthorizeResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthAuthorizeResponse()

  @Serializable
  public data class GetOauthAuthorizeResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthAuthorizeResponse()

  @Serializable
  public data class GetOauthAuthorizeResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOauthAuthorizeResponse()
}

public fun OauthOauthAuthorizeGetClient(configuration: ClientConfiguration = defaultClientConfiguration): OauthOauthAuthorizeGetClient = DefaultOauthOauthAuthorizeGetClient(configuration)

public class DefaultOauthOauthAuthorizeGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : OauthOauthAuthorizeGetClient {
  override suspend fun getOauthAuthorize(
    clientId: String,
    redirectUri: String,
    responseType: String,
    codeChallenge: String?,
    codeChallengeMethod: String?,
    forceLogin: Boolean?,
    lang: String?,
    scope: String?,
    state: String?,
  ): OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponse {
    try {
      val response = configuration.client.`get`("oauth/authorize") {
        url {
          parameters.append("client_id", clientId)
          parameters.append("redirect_uri", redirectUri)
          parameters.append("response_type", responseType)
          if (codeChallenge != null) {
            parameters.append("code_challenge", codeChallenge)
          }
          if (codeChallengeMethod != null) {
            parameters.append("code_challenge_method", codeChallengeMethod)
          }
          if (forceLogin != null) {
            parameters.append("force_login", forceLogin.toString())
          }
          if (lang != null) {
            parameters.append("lang", lang)
          }
          if (scope != null) {
            parameters.append("scope", scope)
          }
          if (state != null) {
            parameters.append("state", state)
          }
        }
      }
      return when (response.status.value) {
        200 -> OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponseSuccess(response.headers)
        400, 401, 404, 429, 503 -> OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponseFailure400(response.body<Error>(), response.headers)
        410 -> OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponseFailure410(response.headers)
        422 -> OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponseFailure(response.body<ValidationError>(), response.headers)
        else -> OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return OauthOauthAuthorizeGetClient.GetOauthAuthorizeResponseUnknownFailure(500)
    }
  }
}
