package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdTranslatePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Translation
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface StatusesApiV1StatusesIdTranslatePostClient {
  /**
   * Translate a status
   */
  public suspend fun postStatusTranslate(request: PostStatusTranslateRequest, id: String): PostStatusTranslateResponse

  @Serializable
  public data class PostStatusTranslateRequest(
    public val lang: String? = null,
  )

  @Serializable
  public sealed class PostStatusTranslateResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusTranslateResponseSuccess(
    public val body: Translation,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusTranslateResponse() {
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
  public data class PostStatusTranslateResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusTranslateResponse()

  @Serializable
  public data class PostStatusTranslateResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusTranslateResponse()

  @Serializable
  public data class PostStatusTranslateResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusTranslateResponse()

  @Serializable
  public data class PostStatusTranslateResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusTranslateResponse()
}

public fun StatusesApiV1StatusesIdTranslatePostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdTranslatePostClient = DefaultStatusesApiV1StatusesIdTranslatePostClient(configuration)

public class DefaultStatusesApiV1StatusesIdTranslatePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdTranslatePostClient {
  override suspend fun postStatusTranslate(request: StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateRequest, id: String): StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/translate".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponseSuccess(response.body<Translation>(), response.headers)
        401, 403, 404, 429, 503 -> StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdTranslatePostClient.PostStatusTranslateResponseUnknownFailure(500)
    }
  }
}
