package org.litote.mastodon.ktor.sdk.mediaApiV1MediaPost.client

import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public class MediaApiV1MediaPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Upload media as an attachment (v1)
   */
  public suspend fun createMedia(form: CreateMediaForm): CreateMediaResponse {
    try {
      val response = configuration.client.post("api/v1/media") {
        setBody(MultiPartFormDataContent(formData {
        append("file", form.file.bytes, Headers.build {
          append(HttpHeaders.ContentType, form.file.contentType.toString())
          append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"" + form.file.filename + "\"")
        })
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
        200 -> CreateMediaResponseSuccess(response.body<MediaAttachment>())
        401, 404, 422, 429, 503 -> CreateMediaResponseFailure401(response.body<Error>())
        410 -> CreateMediaResponseFailure
        else -> CreateMediaResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateMediaResponseUnknownFailure(500)
    }
  }

  public data class CreateMediaForm(
    public val `file`: CreateMediaFormFile,
    public val description: String? = null,
    public val focus: String? = null,
    public val thumbnail: CreateMediaFormFile? = null,
  )

  public data class CreateMediaFormFile(
    public val bytes: ByteArray,
    public val contentType: ContentType,
    public val filename: String = "upload",
  )

  @Serializable
  public sealed class CreateMediaResponse

  @Serializable
  public data class CreateMediaResponseSuccess(
    public val body: MediaAttachment,
  ) : CreateMediaResponse()

  @Serializable
  public data class CreateMediaResponseFailure401(
    public val body: Error,
  ) : CreateMediaResponse()

  @Serializable
  public object CreateMediaResponseFailure : CreateMediaResponse()

  @Serializable
  public data class CreateMediaResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateMediaResponse()
}
