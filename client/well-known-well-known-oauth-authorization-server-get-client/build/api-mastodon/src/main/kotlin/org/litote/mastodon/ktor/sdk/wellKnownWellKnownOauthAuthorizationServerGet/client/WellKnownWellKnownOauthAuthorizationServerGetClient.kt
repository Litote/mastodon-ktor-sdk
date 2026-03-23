package org.litote.mastodon.ktor.sdk.wellKnownWellKnownOauthAuthorizationServerGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.DiscoverOauthServerConfigurationResponse
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class WellKnownWellKnownOauthAuthorizationServerGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Discover OAuth Server Configuration
   */
  public suspend fun getWellKnownOauthAuthorizationServer(): GetWellKnownOauthAuthorizationServerResponse {
    try {
      val response = configuration.client.`get`(".well-known/oauth-authorization-server") {
      }
      return when (response.status.value) {
        200 -> GetWellKnownOauthAuthorizationServerResponseSuccess(response.body<DiscoverOauthServerConfigurationResponse>())
        401, 404, 429, 503 -> GetWellKnownOauthAuthorizationServerResponseFailure401(response.body<Error>())
        410 -> GetWellKnownOauthAuthorizationServerResponseFailure410
        422 -> GetWellKnownOauthAuthorizationServerResponseFailure(response.body<ValidationError>())
        else -> GetWellKnownOauthAuthorizationServerResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetWellKnownOauthAuthorizationServerResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetWellKnownOauthAuthorizationServerResponse

  @Serializable
  public data class GetWellKnownOauthAuthorizationServerResponseSuccess(
    public val body: DiscoverOauthServerConfigurationResponse,
  ) : GetWellKnownOauthAuthorizationServerResponse()

  @Serializable
  public data class GetWellKnownOauthAuthorizationServerResponseFailure401(
    public val body: Error,
  ) : GetWellKnownOauthAuthorizationServerResponse()

  @Serializable
  public object GetWellKnownOauthAuthorizationServerResponseFailure410 : GetWellKnownOauthAuthorizationServerResponse()

  @Serializable
  public data class GetWellKnownOauthAuthorizationServerResponseFailure(
    public val body: ValidationError,
  ) : GetWellKnownOauthAuthorizationServerResponse()

  @Serializable
  public data class GetWellKnownOauthAuthorizationServerResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetWellKnownOauthAuthorizationServerResponse()
}
