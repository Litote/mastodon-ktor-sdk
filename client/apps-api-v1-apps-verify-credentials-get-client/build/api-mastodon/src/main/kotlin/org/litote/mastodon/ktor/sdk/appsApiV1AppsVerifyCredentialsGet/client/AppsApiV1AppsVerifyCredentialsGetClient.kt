package org.litote.mastodon.ktor.sdk.appsApiV1AppsVerifyCredentialsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Application
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class AppsApiV1AppsVerifyCredentialsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Verify your app works
   */
  public suspend fun getAppsVerifyCredentials(): GetAppsVerifyCredentialsResponse {
    try {
      val response = configuration.client.`get`("api/v1/apps/verify_credentials") {
      }
      return when (response.status.value) {
        200 -> GetAppsVerifyCredentialsResponseSuccess(response.body<Application>())
        401, 404, 429, 503 -> GetAppsVerifyCredentialsResponseFailure401(response.body<Error>())
        410 -> GetAppsVerifyCredentialsResponseFailure410
        422 -> GetAppsVerifyCredentialsResponseFailure(response.body<ValidationError>())
        else -> GetAppsVerifyCredentialsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAppsVerifyCredentialsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAppsVerifyCredentialsResponse

  @Serializable
  public data class GetAppsVerifyCredentialsResponseSuccess(
    public val body: Application,
  ) : GetAppsVerifyCredentialsResponse()

  @Serializable
  public data class GetAppsVerifyCredentialsResponseFailure401(
    public val body: Error,
  ) : GetAppsVerifyCredentialsResponse()

  @Serializable
  public object GetAppsVerifyCredentialsResponseFailure410 : GetAppsVerifyCredentialsResponse()

  @Serializable
  public data class GetAppsVerifyCredentialsResponseFailure(
    public val body: ValidationError,
  ) : GetAppsVerifyCredentialsResponse()

  @Serializable
  public data class GetAppsVerifyCredentialsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAppsVerifyCredentialsResponse()
}
