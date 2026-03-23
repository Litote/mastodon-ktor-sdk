package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceTermsOfServiceDateGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedInstanceapiv1instancetermsofservicedategetFcd3eaa4.model.TermsOfService

public class InstanceApiV1InstanceTermsOfServiceDateGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a specific version of the terms of service
   */
  public suspend fun getInstanceTermsOfServiceByDate(date: String): GetInstanceTermsOfServiceByDateResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/terms_of_service/{date}".replace("/{date}", "/${date.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetInstanceTermsOfServiceByDateResponseSuccess(response.body<TermsOfService>())
        401, 404, 429, 503 -> GetInstanceTermsOfServiceByDateResponseFailure401(response.body<Error>())
        410 -> GetInstanceTermsOfServiceByDateResponseFailure410
        422 -> GetInstanceTermsOfServiceByDateResponseFailure(response.body<ValidationError>())
        else -> GetInstanceTermsOfServiceByDateResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceTermsOfServiceByDateResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceTermsOfServiceByDateResponse

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseSuccess(
    public val body: TermsOfService,
  ) : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseFailure401(
    public val body: Error,
  ) : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public object GetInstanceTermsOfServiceByDateResponseFailure410 : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceTermsOfServiceByDateResponse()
}
