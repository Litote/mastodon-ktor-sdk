package org.litote.mastodon.ktor.sdk.oauthOauthRevokePost.client

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

public interface OauthOauthRevokePostClient {
  /**
   * Revoke a token
   */
  public suspend fun postOauthRevoke(request: PostOauthRevokeRequest): PostOauthRevokeResponse

  @Serializable
  public data class PostOauthRevokeRequest(
    @SerialName("client_id")
    public val clientId: String,
    @SerialName("client_secret")
    public val clientSecret: String,
    public val token: String,
  )

  @Serializable
  public sealed class PostOauthRevokeResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostOauthRevokeResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthRevokeResponse() {
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
  public data class PostOauthRevokeResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthRevokeResponse()

  @Serializable
  public data class PostOauthRevokeResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthRevokeResponse()

  @Serializable
  public data class PostOauthRevokeResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthRevokeResponse()

  @Serializable
  public data class PostOauthRevokeResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostOauthRevokeResponse()
}

public fun OauthOauthRevokePostClient(configuration: ClientConfiguration = defaultClientConfiguration): OauthOauthRevokePostClient = DefaultOauthOauthRevokePostClient(configuration)

public class DefaultOauthOauthRevokePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : OauthOauthRevokePostClient {
  override suspend fun postOauthRevoke(request: OauthOauthRevokePostClient.PostOauthRevokeRequest): OauthOauthRevokePostClient.PostOauthRevokeResponse {
    try {
      val response = configuration.client.post("oauth/revoke") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> OauthOauthRevokePostClient.PostOauthRevokeResponseSuccess(response.headers)
        401, 403, 404, 429, 503 -> OauthOauthRevokePostClient.PostOauthRevokeResponseFailure401(response.body<Error>(), response.headers)
        410 -> OauthOauthRevokePostClient.PostOauthRevokeResponseFailure410(response.headers)
        422 -> OauthOauthRevokePostClient.PostOauthRevokeResponseFailure(response.body<ValidationError>(), response.headers)
        else -> OauthOauthRevokePostClient.PostOauthRevokeResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return OauthOauthRevokePostClient.PostOauthRevokeResponseUnknownFailure(500)
    }
  }
}
