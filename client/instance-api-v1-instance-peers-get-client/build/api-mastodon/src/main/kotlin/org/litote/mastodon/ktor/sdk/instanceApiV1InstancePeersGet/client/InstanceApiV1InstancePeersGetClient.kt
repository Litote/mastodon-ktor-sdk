package org.litote.mastodon.ktor.sdk.instanceApiV1InstancePeersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstancePeersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * List of connected domains
   */
  public suspend fun getInstancePeers(): GetInstancePeersResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/peers") {
      }
      return when (response.status.value) {
        200 -> GetInstancePeersResponseSuccess(response.body<List<String>>())
        401, 404, 429, 503 -> GetInstancePeersResponseFailure401(response.body<Error>())
        410 -> GetInstancePeersResponseFailure410
        422 -> GetInstancePeersResponseFailure(response.body<ValidationError>())
        else -> GetInstancePeersResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstancePeersResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstancePeersResponse

  @Serializable
  public data class GetInstancePeersResponseSuccess(
    public val body: List<String>,
  ) : GetInstancePeersResponse()

  @Serializable
  public data class GetInstancePeersResponseFailure401(
    public val body: Error,
  ) : GetInstancePeersResponse()

  @Serializable
  public object GetInstancePeersResponseFailure410 : GetInstancePeersResponse()

  @Serializable
  public data class GetInstancePeersResponseFailure(
    public val body: ValidationError,
  ) : GetInstancePeersResponse()

  @Serializable
  public data class GetInstancePeersResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstancePeersResponse()
}
