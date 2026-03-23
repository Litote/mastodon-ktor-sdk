package org.litote.mastodon.ktor.sdk.preferencesApiV1PreferencesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class PreferencesApiV1PreferencesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View user preferences
   */
  public suspend fun getPreferences(): GetPreferencesResponse {
    try {
      val response = configuration.client.`get`("api/v1/preferences") {
      }
      return when (response.status.value) {
        200 -> GetPreferencesResponseSuccess
        401, 404, 429, 503 -> GetPreferencesResponseFailure401(response.body<Error>())
        410 -> GetPreferencesResponseFailure410
        422 -> GetPreferencesResponseFailure(response.body<ValidationError>())
        else -> GetPreferencesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetPreferencesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetPreferencesResponse

  @Serializable
  public object GetPreferencesResponseSuccess : GetPreferencesResponse()

  @Serializable
  public data class GetPreferencesResponseFailure401(
    public val body: Error,
  ) : GetPreferencesResponse()

  @Serializable
  public object GetPreferencesResponseFailure410 : GetPreferencesResponse()

  @Serializable
  public data class GetPreferencesResponseFailure(
    public val body: ValidationError,
  ) : GetPreferencesResponse()

  @Serializable
  public data class GetPreferencesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetPreferencesResponse()
}
