package org.litote.mastodon.ktor.sdk.tagsApiV1TagsIdUnfeaturePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public interface TagsApiV1TagsIdUnfeaturePostClient {
  /**
   * Unfeature a hashtag
   */
  public suspend fun postTagUnfeature(id: String): PostTagUnfeatureResponse

  @Serializable
  public sealed class PostTagUnfeatureResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostTagUnfeatureResponseSuccess(
    public val body: Tag,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfeatureResponse() {
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
  public data class PostTagUnfeatureResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfeatureResponse()

  @Serializable
  public data class PostTagUnfeatureResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfeatureResponse()

  @Serializable
  public data class PostTagUnfeatureResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfeatureResponse()

  @Serializable
  public data class PostTagUnfeatureResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfeatureResponse()
}

public fun TagsApiV1TagsIdUnfeaturePostClient(configuration: ClientConfiguration = defaultClientConfiguration): TagsApiV1TagsIdUnfeaturePostClient = DefaultTagsApiV1TagsIdUnfeaturePostClient(configuration)

public class DefaultTagsApiV1TagsIdUnfeaturePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TagsApiV1TagsIdUnfeaturePostClient {
  override suspend fun postTagUnfeature(id: String): TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{id}/unfeature".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponseSuccess(response.body<Tag>(), response.headers)
        401, 404, 429, 503 -> TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponseFailure401(response.body<Error>(), response.headers)
        410 -> TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponseFailure410(response.headers)
        422 -> TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TagsApiV1TagsIdUnfeaturePostClient.PostTagUnfeatureResponseUnknownFailure(500)
    }
  }
}
