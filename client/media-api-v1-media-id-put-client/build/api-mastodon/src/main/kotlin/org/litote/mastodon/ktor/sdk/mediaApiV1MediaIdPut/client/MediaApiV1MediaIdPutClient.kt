package org.litote.mastodon.ktor.sdk.mediaApiV1MediaIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPathPart
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public class MediaApiV1MediaIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Update media attachment
   */
  public suspend fun updateMedia(form: UpdateMediaForm, id: String): UpdateMediaResponse {
    try {
      val response = configuration.client.put("api/v1/media/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(MultiPartFormDataContent(formData {
        form.description?.let { value ->
          append("description", value)
        }
        form.focus?.let { value ->
          append("focus", value)
        }
        form.thumbnail?.let { value ->
          append("thumbnail", value.bytes, Headers.build {
            append(HttpHeaders.ContentType, value.contentType.toString())
            append(HttpHeaders.ContentDisposition, "form-data; name=\"thumbnail\"; filename=\"" + value.filename + "\"")
          })
        }
        }))
      }
      return when (response.status.value) {
        200 -> UpdateMediaResponseSuccess(response.body<MediaAttachment>())
        401, 404, 422, 429, 503 -> UpdateMediaResponseFailure401(response.body<Error>())
        410 -> UpdateMediaResponseFailure
        else -> UpdateMediaResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateMediaResponseUnknownFailure(500)
    }
  }

  public data class UpdateMediaForm(
    public val description: String? = null,
    public val focus: String? = null,
    public val thumbnail: UpdateMediaFormFile? = null,
  )

  public data class UpdateMediaFormFile(
    public val bytes: ByteArray,
    public val contentType: ContentType,
    public val filename: String = "upload",
  )

  @Serializable
  public sealed class UpdateMediaResponse

  @Serializable
  public data class UpdateMediaResponseSuccess(
    public val body: MediaAttachment,
  ) : UpdateMediaResponse()

  @Serializable
  public data class UpdateMediaResponseFailure401(
    public val body: Error,
  ) : UpdateMediaResponse()

  @Serializable
  public object UpdateMediaResponseFailure : UpdateMediaResponse()

  @Serializable
  public data class UpdateMediaResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateMediaResponse()
}
