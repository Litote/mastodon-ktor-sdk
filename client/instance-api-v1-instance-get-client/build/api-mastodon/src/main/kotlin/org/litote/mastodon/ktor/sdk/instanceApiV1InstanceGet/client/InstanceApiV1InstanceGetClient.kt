package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.V1Instance
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstanceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View server information (v1)
   */
  public suspend fun getInstance(): GetInstanceResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance") {
      }
      return when (response.status.value) {
        200 -> GetInstanceResponseSuccess(response.body<V1Instance>())
        401, 404, 429, 503 -> GetInstanceResponseFailure401(response.body<Error>())
        410 -> GetInstanceResponseFailure410
        422 -> GetInstanceResponseFailure(response.body<ValidationError>())
        else -> GetInstanceResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceResponse

  @Serializable
  public data class GetInstanceResponseSuccess(
    public val body: V1Instance,
  ) : GetInstanceResponse()

  @Serializable
  public data class GetInstanceResponseFailure401(
    public val body: Error,
  ) : GetInstanceResponse()

  @Serializable
  public object GetInstanceResponseFailure410 : GetInstanceResponse()

  @Serializable
  public data class GetInstanceResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceResponse()

  @Serializable
  public data class GetInstanceResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceResponse()
}
