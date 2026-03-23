package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceTranslationLanguagesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstanceTranslationLanguagesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View translation languages
   */
  public suspend fun getInstanceTranslationLanguages(): GetInstanceTranslationLanguagesResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/translation_languages") {
      }
      return when (response.status.value) {
        200 -> GetInstanceTranslationLanguagesResponseSuccess
        401, 404, 429, 503 -> GetInstanceTranslationLanguagesResponseFailure401(response.body<Error>())
        410 -> GetInstanceTranslationLanguagesResponseFailure410
        422 -> GetInstanceTranslationLanguagesResponseFailure(response.body<ValidationError>())
        else -> GetInstanceTranslationLanguagesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceTranslationLanguagesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceTranslationLanguagesResponse

  @Serializable
  public object GetInstanceTranslationLanguagesResponseSuccess : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseFailure401(
    public val body: Error,
  ) : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public object GetInstanceTranslationLanguagesResponseFailure410 : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceTranslationLanguagesResponse()
}
