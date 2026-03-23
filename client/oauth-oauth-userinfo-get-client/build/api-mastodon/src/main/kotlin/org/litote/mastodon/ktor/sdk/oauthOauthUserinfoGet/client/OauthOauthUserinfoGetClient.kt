package org.litote.mastodon.ktor.sdk.oauthOauthUserinfoGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class OauthOauthUserinfoGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Retrieve user information
   */
  public suspend fun getOauthUserinfo(): GetOauthUserinfoResponse {
    try {
      val response = configuration.client.`get`("oauth/userinfo") {
      }
      return when (response.status.value) {
        200 -> GetOauthUserinfoResponseSuccess
        401, 403, 404, 429, 503 -> GetOauthUserinfoResponseFailure401(response.body<Error>())
        410 -> GetOauthUserinfoResponseFailure410
        422 -> GetOauthUserinfoResponseFailure(response.body<ValidationError>())
        else -> GetOauthUserinfoResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetOauthUserinfoResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetOauthUserinfoResponse

  @Serializable
  public object GetOauthUserinfoResponseSuccess : GetOauthUserinfoResponse()

  @Serializable
  public data class GetOauthUserinfoResponseFailure401(
    public val body: Error,
  ) : GetOauthUserinfoResponse()

  @Serializable
  public object GetOauthUserinfoResponseFailure410 : GetOauthUserinfoResponse()

  @Serializable
  public data class GetOauthUserinfoResponseFailure(
    public val body: ValidationError,
  ) : GetOauthUserinfoResponse()

  @Serializable
  public data class GetOauthUserinfoResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetOauthUserinfoResponse()
}
