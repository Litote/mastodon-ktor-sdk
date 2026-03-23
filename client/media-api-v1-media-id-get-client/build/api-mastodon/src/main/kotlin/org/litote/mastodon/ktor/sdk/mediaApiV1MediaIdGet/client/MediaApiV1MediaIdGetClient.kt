package org.litote.mastodon.ktor.sdk.mediaApiV1MediaIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public class MediaApiV1MediaIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get media attachment
   */
  public suspend fun getMedia(id: String): GetMediaResponse {
    try {
      val response = configuration.client.`get`("api/v1/media/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetMediaResponseSuccess200(response.body<MediaAttachment>())
        206 -> GetMediaResponseSuccess
        401, 404, 422, 429, 503 -> GetMediaResponseFailure401(response.body<Error>())
        410 -> GetMediaResponseFailure
        else -> GetMediaResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetMediaResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetMediaResponse

  @Serializable
  public data class GetMediaResponseSuccess200(
    public val body: MediaAttachment,
  ) : GetMediaResponse()

  @Serializable
  public object GetMediaResponseSuccess : GetMediaResponse()

  @Serializable
  public data class GetMediaResponseFailure401(
    public val body: Error,
  ) : GetMediaResponse()

  @Serializable
  public object GetMediaResponseFailure : GetMediaResponse()

  @Serializable
  public data class GetMediaResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetMediaResponse()
}
