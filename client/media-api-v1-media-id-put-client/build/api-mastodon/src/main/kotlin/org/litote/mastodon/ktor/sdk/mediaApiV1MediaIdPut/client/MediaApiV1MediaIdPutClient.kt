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
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

public interface MediaApiV1MediaIdPutClient {
  /**
   * Update media attachment
   */
  public suspend fun updateMedia(form: UpdateMediaForm, id: String): UpdateMediaResponse

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
  public sealed class UpdateMediaResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateMediaResponseSuccess(
    public val body: MediaAttachment,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateMediaResponse() {
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
  public data class UpdateMediaResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateMediaResponse()

  @Serializable
  public data class UpdateMediaResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateMediaResponse()

  @Serializable
  public data class UpdateMediaResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateMediaResponse()
}

public fun MediaApiV1MediaIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): MediaApiV1MediaIdPutClient = DefaultMediaApiV1MediaIdPutClient(configuration)

public class DefaultMediaApiV1MediaIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MediaApiV1MediaIdPutClient {
  override suspend fun updateMedia(form: MediaApiV1MediaIdPutClient.UpdateMediaForm, id: String): MediaApiV1MediaIdPutClient.UpdateMediaResponse {
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
        200 -> MediaApiV1MediaIdPutClient.UpdateMediaResponseSuccess(response.body<MediaAttachment>(), response.headers)
        401, 404, 422, 429, 503 -> MediaApiV1MediaIdPutClient.UpdateMediaResponseFailure401(response.body<Error>(), response.headers)
        410 -> MediaApiV1MediaIdPutClient.UpdateMediaResponseFailure(response.headers)
        else -> MediaApiV1MediaIdPutClient.UpdateMediaResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MediaApiV1MediaIdPutClient.UpdateMediaResponseUnknownFailure(500)
    }
  }
}
