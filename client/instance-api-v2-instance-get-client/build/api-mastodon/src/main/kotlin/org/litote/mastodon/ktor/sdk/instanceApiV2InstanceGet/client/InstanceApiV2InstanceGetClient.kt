package org.litote.mastodon.ktor.sdk.instanceApiV2InstanceGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Instance
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV2InstanceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View server information
   */
  public suspend fun getInstanceV2(): GetInstanceV2Response {
    try {
      val response = configuration.client.`get`("api/v2/instance") {
      }
      return when (response.status.value) {
        200 -> GetInstanceV2ResponseSuccess(response.body<Instance>())
        401, 404, 429, 503 -> GetInstanceV2ResponseFailure401(response.body<Error>())
        410 -> GetInstanceV2ResponseFailure410
        422 -> GetInstanceV2ResponseFailure(response.body<ValidationError>())
        else -> GetInstanceV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceV2Response

  @Serializable
  public data class GetInstanceV2ResponseSuccess(
    public val body: Instance,
  ) : GetInstanceV2Response()

  @Serializable
  public data class GetInstanceV2ResponseFailure401(
    public val body: Error,
  ) : GetInstanceV2Response()

  @Serializable
  public object GetInstanceV2ResponseFailure410 : GetInstanceV2Response()

  @Serializable
  public data class GetInstanceV2ResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceV2Response()

  @Serializable
  public data class GetInstanceV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceV2Response()
}
