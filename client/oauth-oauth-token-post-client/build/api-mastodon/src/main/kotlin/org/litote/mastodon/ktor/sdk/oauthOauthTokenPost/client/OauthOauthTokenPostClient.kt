package org.litote.mastodon.ktor.sdk.oauthOauthTokenPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountspostOauthoauthtokenpost.model.Token

public interface OauthOauthTokenPostClient {
  /**
   * Obtain a token
   */
  public suspend fun postOauthToken(request: PostOauthTokenRequest): PostOauthTokenResponse

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
  public sealed class PostOauthTokenResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostOauthTokenResponseSuccess(
    public val body: Token,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthTokenResponse() {
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
  public data class PostOauthTokenResponseFailure400(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthTokenResponse()

  @Serializable
  public data class PostOauthTokenResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthTokenResponse()

  @Serializable
  public data class PostOauthTokenResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthTokenResponse()

  @Serializable
  public data class PostOauthTokenResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthTokenResponse()
}

public fun OauthOauthTokenPostClient(configuration: ClientConfiguration = defaultClientConfiguration): OauthOauthTokenPostClient = DefaultOauthOauthTokenPostClient(configuration)

public class DefaultOauthOauthTokenPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : OauthOauthTokenPostClient {
  override suspend fun postOauthToken(request: OauthOauthTokenPostClient.PostOauthTokenRequest): OauthOauthTokenPostClient.PostOauthTokenResponse {
    try {
      val response = configuration.client.post("oauth/token") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> OauthOauthTokenPostClient.PostOauthTokenResponseSuccess(response.body<Token>(), response.headers)
        400, 401, 404, 429, 503 -> OauthOauthTokenPostClient.PostOauthTokenResponseFailure400(response.body<Error>(), response.headers)
        410 -> OauthOauthTokenPostClient.PostOauthTokenResponseFailure410(response.headers)
        422 -> OauthOauthTokenPostClient.PostOauthTokenResponseFailure(response.body<ValidationError>(), response.headers)
        else -> OauthOauthTokenPostClient.PostOauthTokenResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return OauthOauthTokenPostClient.PostOauthTokenResponseUnknownFailure(500)
    }
  }
}
