package org.litote.mastodon.ktor.sdk.oauthOauthAuthorizeGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class OauthOauthAuthorizeGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
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
  ): GetOauthAuthorizeResponse {
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
        200 -> GetOauthAuthorizeResponseSuccess
        400, 401, 404, 429, 503 -> GetOauthAuthorizeResponseFailure400(response.body<Error>())
        410 -> GetOauthAuthorizeResponseFailure410
        422 -> GetOauthAuthorizeResponseFailure(response.body<ValidationError>())
        else -> GetOauthAuthorizeResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetOauthAuthorizeResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetOauthAuthorizeResponse

  @Serializable
  public object GetOauthAuthorizeResponseSuccess : GetOauthAuthorizeResponse()

  @Serializable
  public data class GetOauthAuthorizeResponseFailure400(
    public val body: Error,
  ) : GetOauthAuthorizeResponse()

  @Serializable
  public object GetOauthAuthorizeResponseFailure410 : GetOauthAuthorizeResponse()

  @Serializable
  public data class GetOauthAuthorizeResponseFailure(
    public val body: ValidationError,
  ) : GetOauthAuthorizeResponse()

  @Serializable
  public data class GetOauthAuthorizeResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetOauthAuthorizeResponse()
}
