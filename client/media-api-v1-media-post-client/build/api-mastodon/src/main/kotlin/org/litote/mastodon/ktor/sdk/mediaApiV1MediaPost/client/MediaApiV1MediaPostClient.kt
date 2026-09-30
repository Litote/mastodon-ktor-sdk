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
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public interface MediaApiV1MediaPostClient {
  /**
   * Upload media as an attachment (v1)
   */
  public suspend fun createMedia(form: CreateMediaForm): CreateMediaResponse

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
  public sealed class CreateMediaResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateMediaResponseSuccess(
    public val body: MediaAttachment,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class CreateMediaResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaResponse()

  @Serializable
  public data class CreateMediaResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaResponse()

  @Serializable
  public data class CreateMediaResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaResponse()
}

public fun MediaApiV1MediaPostClient(configuration: ClientConfiguration = defaultClientConfiguration): MediaApiV1MediaPostClient = DefaultMediaApiV1MediaPostClient(configuration)

public class DefaultMediaApiV1MediaPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MediaApiV1MediaPostClient {
  override suspend fun createMedia(form: MediaApiV1MediaPostClient.CreateMediaForm): MediaApiV1MediaPostClient.CreateMediaResponse {
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
        200 -> MediaApiV1MediaPostClient.CreateMediaResponseSuccess(response.body<MediaAttachment>(), response.headers)
        401, 404, 422, 429, 503 -> MediaApiV1MediaPostClient.CreateMediaResponseFailure401(response.body<Error>(), response.headers)
        410 -> MediaApiV1MediaPostClient.CreateMediaResponseFailure(response.headers)
        else -> MediaApiV1MediaPostClient.CreateMediaResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MediaApiV1MediaPostClient.CreateMediaResponseUnknownFailure(500)
    }
  }
}
