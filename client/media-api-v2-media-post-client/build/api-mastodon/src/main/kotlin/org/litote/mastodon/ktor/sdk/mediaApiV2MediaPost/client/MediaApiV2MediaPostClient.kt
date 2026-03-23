package org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client

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

public class MediaApiV2MediaPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Upload media as an attachment (async)
   */
  public suspend fun createMediaV2(form: CreateMediaV2Form): CreateMediaV2Response {
    try {
      val response = configuration.client.post("api/v2/media") {
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
        200, 202 -> CreateMediaV2ResponseSuccess(response.body<MediaAttachment>())
        401, 404, 422, 429, 500, 503 -> CreateMediaV2ResponseFailure401(response.body<Error>())
        410 -> CreateMediaV2ResponseFailure
        else -> CreateMediaV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateMediaV2ResponseUnknownFailure(500)
    }
  }

  public data class CreateMediaV2Form(
    public val `file`: CreateMediaV2FormFile,
    public val description: String? = null,
    public val focus: String? = null,
    public val thumbnail: CreateMediaV2FormFile? = null,
  )

  public data class CreateMediaV2FormFile(
    public val bytes: ByteArray,
    public val contentType: ContentType,
    public val filename: String = "upload",
  )

  @Serializable
  public sealed class CreateMediaV2Response

  @Serializable
  public data class CreateMediaV2ResponseSuccess(
    public val body: MediaAttachment,
  ) : CreateMediaV2Response()

  @Serializable
  public data class CreateMediaV2ResponseFailure401(
    public val body: Error,
  ) : CreateMediaV2Response()

  @Serializable
  public object CreateMediaV2ResponseFailure : CreateMediaV2Response()

  @Serializable
  public data class CreateMediaV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateMediaV2Response()
}
