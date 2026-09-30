package org.litote.mastodon.ktor.sdk.tagsApiV1TagsIdFeaturePost.client

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

public interface TagsApiV1TagsIdFeaturePostClient {
  /**
   * Feature a hashtag
   */
  public suspend fun postTagFeature(id: String): PostTagFeatureResponse

  @Serializable
  public sealed class PostTagFeatureResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostTagFeatureResponseSuccess(
    public val body: Tag,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFeatureResponse() {
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
  public data class PostTagFeatureResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFeatureResponse()

  @Serializable
  public data class PostTagFeatureResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFeatureResponse()

  @Serializable
  public data class PostTagFeatureResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFeatureResponse()

  @Serializable
  public data class PostTagFeatureResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFeatureResponse()
}

public fun TagsApiV1TagsIdFeaturePostClient(configuration: ClientConfiguration = defaultClientConfiguration): TagsApiV1TagsIdFeaturePostClient = DefaultTagsApiV1TagsIdFeaturePostClient(configuration)

public class DefaultTagsApiV1TagsIdFeaturePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TagsApiV1TagsIdFeaturePostClient {
  override suspend fun postTagFeature(id: String): TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{id}/feature".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponseSuccess(response.body<Tag>(), response.headers)
        401, 404, 429, 503 -> TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponseFailure401(response.body<Error>(), response.headers)
        410 -> TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponseFailure410(response.headers)
        422 -> TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TagsApiV1TagsIdFeaturePostClient.PostTagFeatureResponseUnknownFailure(500)
    }
  }
}
