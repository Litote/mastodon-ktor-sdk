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
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public interface MediaApiV2MediaPostClient {
  /**
   * Upload media as an attachment (async)
   */
  public suspend fun createMediaV2(form: CreateMediaV2Form): CreateMediaV2Response

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
  public sealed class CreateMediaV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateMediaV2ResponseSuccess(
    public val body: MediaAttachment,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaV2Response() {
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
  public data class CreateMediaV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaV2Response()

  @Serializable
  public data class CreateMediaV2ResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaV2Response()

  @Serializable
  public data class CreateMediaV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateMediaV2Response()
}

public fun MediaApiV2MediaPostClient(configuration: ClientConfiguration = defaultClientConfiguration): MediaApiV2MediaPostClient = DefaultMediaApiV2MediaPostClient(configuration)

public class DefaultMediaApiV2MediaPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MediaApiV2MediaPostClient {
  override suspend fun createMediaV2(form: MediaApiV2MediaPostClient.CreateMediaV2Form): MediaApiV2MediaPostClient.CreateMediaV2Response {
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
        200, 202 -> MediaApiV2MediaPostClient.CreateMediaV2ResponseSuccess(response.body<MediaAttachment>(), response.headers)
        401, 404, 422, 429, 500, 503 -> MediaApiV2MediaPostClient.CreateMediaV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> MediaApiV2MediaPostClient.CreateMediaV2ResponseFailure(response.headers)
        else -> MediaApiV2MediaPostClient.CreateMediaV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MediaApiV2MediaPostClient.CreateMediaV2ResponseUnknownFailure(500)
    }
  }
}
