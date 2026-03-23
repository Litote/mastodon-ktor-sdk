package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceExtendedDescriptionGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.ExtendedDescription
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstanceExtendedDescriptionGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View extended description
   */
  public suspend fun getInstanceExtendedDescription(): GetInstanceExtendedDescriptionResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/extended_description") {
      }
      return when (response.status.value) {
        200 -> GetInstanceExtendedDescriptionResponseSuccess(response.body<ExtendedDescription>())
        401, 404, 429, 503 -> GetInstanceExtendedDescriptionResponseFailure401(response.body<Error>())
        410 -> GetInstanceExtendedDescriptionResponseFailure410
        422 -> GetInstanceExtendedDescriptionResponseFailure(response.body<ValidationError>())
        else -> GetInstanceExtendedDescriptionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceExtendedDescriptionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceExtendedDescriptionResponse

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseSuccess(
    public val body: ExtendedDescription,
  ) : GetInstanceExtendedDescriptionResponse()

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseFailure401(
    public val body: Error,
  ) : GetInstanceExtendedDescriptionResponse()

  @Serializable
  public object GetInstanceExtendedDescriptionResponseFailure410 : GetInstanceExtendedDescriptionResponse()

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceExtendedDescriptionResponse()

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceExtendedDescriptionResponse()
}
