package org.litote.mastodon.ktor.sdk.instanceApiV1InstancePrivacyPolicyGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.PrivacyPolicy
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstancePrivacyPolicyGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View privacy policy
   */
  public suspend fun getInstancePrivacyPolicy(): GetInstancePrivacyPolicyResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/privacy_policy") {
      }
      return when (response.status.value) {
        200 -> GetInstancePrivacyPolicyResponseSuccess(response.body<PrivacyPolicy>())
        401, 404, 429, 503 -> GetInstancePrivacyPolicyResponseFailure401(response.body<Error>())
        410 -> GetInstancePrivacyPolicyResponseFailure410
        422 -> GetInstancePrivacyPolicyResponseFailure(response.body<ValidationError>())
        else -> GetInstancePrivacyPolicyResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstancePrivacyPolicyResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstancePrivacyPolicyResponse

  @Serializable
  public data class GetInstancePrivacyPolicyResponseSuccess(
    public val body: PrivacyPolicy,
  ) : GetInstancePrivacyPolicyResponse()

  @Serializable
  public data class GetInstancePrivacyPolicyResponseFailure401(
    public val body: Error,
  ) : GetInstancePrivacyPolicyResponse()

  @Serializable
  public object GetInstancePrivacyPolicyResponseFailure410 : GetInstancePrivacyPolicyResponse()

  @Serializable
  public data class GetInstancePrivacyPolicyResponseFailure(
    public val body: ValidationError,
  ) : GetInstancePrivacyPolicyResponse()

  @Serializable
  public data class GetInstancePrivacyPolicyResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstancePrivacyPolicyResponse()
}
