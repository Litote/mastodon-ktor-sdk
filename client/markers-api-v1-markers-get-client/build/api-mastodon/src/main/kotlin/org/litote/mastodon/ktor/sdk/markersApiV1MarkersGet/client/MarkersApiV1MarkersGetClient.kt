package org.litote.mastodon.ktor.sdk.markersApiV1MarkersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum
import org.litote.mastodon.ktor.sdk.sharedMarkersapiv1markersgetMarkersapiv1markerspost.model.Marker

public class MarkersApiV1MarkersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get saved timeline positions
   */
  public suspend fun getMarkers(timeline: List<FilterContextEnum>? = null): GetMarkersResponse {
    try {
      val response = configuration.client.`get`("api/v1/markers") {
        url {
          if (timeline != null) {
            parameters.append("timeline", timeline.joinToString(","))
          }
        }
      }
      return when (response.status.value) {
        200 -> GetMarkersResponseSuccess(response.body<Map<String, Marker>>())
        401, 404, 429, 503 -> GetMarkersResponseFailure401(response.body<Error>())
        410 -> GetMarkersResponseFailure410
        422 -> GetMarkersResponseFailure(response.body<ValidationError>())
        else -> GetMarkersResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetMarkersResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetMarkersResponse

  @Serializable
  public data class GetMarkersResponseSuccess(
    public val body: Map<String, Marker>,
  ) : GetMarkersResponse()

  @Serializable
  public data class GetMarkersResponseFailure401(
    public val body: Error,
  ) : GetMarkersResponse()

  @Serializable
  public object GetMarkersResponseFailure410 : GetMarkersResponse()

  @Serializable
  public data class GetMarkersResponseFailure(
    public val body: ValidationError,
  ) : GetMarkersResponse()

  @Serializable
  public data class GetMarkersResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetMarkersResponse()
}
