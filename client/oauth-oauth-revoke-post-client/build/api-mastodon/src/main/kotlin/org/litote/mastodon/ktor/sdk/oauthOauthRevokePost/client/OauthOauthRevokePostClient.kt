package org.litote.mastodon.ktor.sdk.oauthOauthRevokePost.client

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

public class OauthOauthRevokePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Revoke a token
   */
  public suspend fun postOauthRevoke(request: PostOauthRevokeRequest): PostOauthRevokeResponse {
    try {
      val response = configuration.client.post("oauth/revoke") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostOauthRevokeResponseSuccess
        401, 403, 404, 429, 503 -> PostOauthRevokeResponseFailure401(response.body<Error>())
        410 -> PostOauthRevokeResponseFailure410
        422 -> PostOauthRevokeResponseFailure(response.body<ValidationError>())
        else -> PostOauthRevokeResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostOauthRevokeResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostOauthRevokeRequest(
    @SerialName("client_id")
    public val clientId: String,
    @SerialName("client_secret")
    public val clientSecret: String,
    public val token: String,
  )

  @Serializable
  public sealed class PostOauthRevokeResponse

  @Serializable
  public object PostOauthRevokeResponseSuccess : PostOauthRevokeResponse()

  @Serializable
  public data class PostOauthRevokeResponseFailure401(
    public val body: Error,
  ) : PostOauthRevokeResponse()

  @Serializable
  public object PostOauthRevokeResponseFailure410 : PostOauthRevokeResponse()

  @Serializable
  public data class PostOauthRevokeResponseFailure(
    public val body: ValidationError,
  ) : PostOauthRevokeResponse()

  @Serializable
  public data class PostOauthRevokeResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostOauthRevokeResponse()
}
