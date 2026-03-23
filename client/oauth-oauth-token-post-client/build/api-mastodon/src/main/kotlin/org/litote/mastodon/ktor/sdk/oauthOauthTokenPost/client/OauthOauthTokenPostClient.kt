package org.litote.mastodon.ktor.sdk.oauthOauthTokenPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountspostOauthoauthtokenpost.model.Token

public class OauthOauthTokenPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Obtain a token
   */
  public suspend fun postOauthToken(request: PostOauthTokenRequest): PostOauthTokenResponse {
    try {
      val response = configuration.client.post("oauth/token") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostOauthTokenResponseSuccess(response.body<Token>())
        400, 401, 404, 429, 503 -> PostOauthTokenResponseFailure400(response.body<Error>())
        410 -> PostOauthTokenResponseFailure410
        422 -> PostOauthTokenResponseFailure(response.body<ValidationError>())
        else -> PostOauthTokenResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostOauthTokenResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostOauthTokenRequest(
    @SerialName("client_id")
    public val clientId: String,
    @SerialName("client_secret")
    public val clientSecret: String,
    public val code: String,
    @SerialName("code_verifier")
    public val codeVerifier: String? = null,
    @SerialName("grant_type")
    public val grantType: String,
    @SerialName("redirect_uri")
    public val redirectUri: String,
    public val scope: String? = "read",
  )

  @Serializable
  public sealed class PostOauthTokenResponse

  @Serializable
  public data class PostOauthTokenResponseSuccess(
    public val body: Token,
  ) : PostOauthTokenResponse()

  @Serializable
  public data class PostOauthTokenResponseFailure400(
    public val body: Error,
  ) : PostOauthTokenResponse()

  @Serializable
  public object PostOauthTokenResponseFailure410 : PostOauthTokenResponse()

  @Serializable
  public data class PostOauthTokenResponseFailure(
    public val body: ValidationError,
  ) : PostOauthTokenResponse()

  @Serializable
  public data class PostOauthTokenResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostOauthTokenResponse()
}
