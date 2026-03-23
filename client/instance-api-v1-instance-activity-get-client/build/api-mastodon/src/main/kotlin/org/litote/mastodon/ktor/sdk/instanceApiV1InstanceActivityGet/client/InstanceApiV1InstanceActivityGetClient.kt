package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceActivityGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstanceActivityGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Weekly activity
   */
  public suspend fun getInstanceActivity(): GetInstanceActivityResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/activity") {
      }
      return when (response.status.value) {
        200 -> GetInstanceActivityResponseSuccess(response.body<List<JsonElement>>())
        401, 404, 429, 503 -> GetInstanceActivityResponseFailure401(response.body<Error>())
        410 -> GetInstanceActivityResponseFailure410
        422 -> GetInstanceActivityResponseFailure(response.body<ValidationError>())
        else -> GetInstanceActivityResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceActivityResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceActivityResponse

  @Serializable
  public data class GetInstanceActivityResponseSuccess(
    public val body: List<JsonElement>,
  ) : GetInstanceActivityResponse()

  @Serializable
  public data class GetInstanceActivityResponseFailure401(
    public val body: Error,
  ) : GetInstanceActivityResponse()

  @Serializable
  public object GetInstanceActivityResponseFailure410 : GetInstanceActivityResponse()

  @Serializable
  public data class GetInstanceActivityResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceActivityResponse()

  @Serializable
  public data class GetInstanceActivityResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceActivityResponse()
}
