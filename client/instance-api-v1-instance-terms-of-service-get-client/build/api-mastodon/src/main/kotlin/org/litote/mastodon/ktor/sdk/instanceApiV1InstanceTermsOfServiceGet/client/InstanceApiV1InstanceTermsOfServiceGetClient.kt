package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceTermsOfServiceGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedInstanceapiv1instancetermsofservicedategetFcd3eaa4.model.TermsOfService

public class InstanceApiV1InstanceTermsOfServiceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View terms of service
   */
  public suspend fun getInstanceTermsOfService(): GetInstanceTermsOfServiceResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/terms_of_service") {
      }
      return when (response.status.value) {
        200 -> GetInstanceTermsOfServiceResponseSuccess(response.body<TermsOfService>())
        401, 404, 429, 503 -> GetInstanceTermsOfServiceResponseFailure401(response.body<Error>())
        410 -> GetInstanceTermsOfServiceResponseFailure410
        422 -> GetInstanceTermsOfServiceResponseFailure(response.body<ValidationError>())
        else -> GetInstanceTermsOfServiceResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceTermsOfServiceResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceTermsOfServiceResponse

  @Serializable
  public data class GetInstanceTermsOfServiceResponseSuccess(
    public val body: TermsOfService,
  ) : GetInstanceTermsOfServiceResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceResponseFailure401(
    public val body: Error,
  ) : GetInstanceTermsOfServiceResponse()

  @Serializable
  public object GetInstanceTermsOfServiceResponseFailure410 : GetInstanceTermsOfServiceResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceTermsOfServiceResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceTermsOfServiceResponse()
}
