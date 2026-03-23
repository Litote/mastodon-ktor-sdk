package org.litote.mastodon.ktor.sdk.markersApiV1MarkersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedMarkersapiv1markersgetMarkersapiv1markerspost.model.Marker

public class MarkersApiV1MarkersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Save your position in a timeline
   */
  public suspend fun createMarker(request: CreateMarkerRequest): CreateMarkerResponse {
    try {
      val response = configuration.client.post("api/v1/markers") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateMarkerResponseSuccess(response.body<Map<String, Marker>>())
        401, 404, 429, 503 -> CreateMarkerResponseFailure401(response.body<Error>())
        410 -> CreateMarkerResponseFailure410
        422 -> CreateMarkerResponseFailure(response.body<ValidationError>())
        else -> CreateMarkerResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateMarkerResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateMarkerRequest(
    public val home: Home? = null,
    public val notifications: Notifications? = null,
  ) {
    @Serializable
    public data class Home(
      @SerialName("last_read_id")
      public val lastReadId: String? = null,
    )

    @Serializable
    public data class Notifications(
      @SerialName("last_read_id")
      public val lastReadId: String? = null,
    )
  }

  @Serializable
  public sealed class CreateMarkerResponse

  @Serializable
  public data class CreateMarkerResponseSuccess(
    public val body: Map<String, Marker>,
  ) : CreateMarkerResponse()

  @Serializable
  public data class CreateMarkerResponseFailure401(
    public val body: Error,
  ) : CreateMarkerResponse()

  @Serializable
  public object CreateMarkerResponseFailure410 : CreateMarkerResponse()

  @Serializable
  public data class CreateMarkerResponseFailure(
    public val body: ValidationError,
  ) : CreateMarkerResponse()

  @Serializable
  public data class CreateMarkerResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateMarkerResponse()
}
