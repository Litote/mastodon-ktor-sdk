package org.litote.mastodon.ktor.sdk.tagsApiV1TagsNameFollowPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.client.OperationResponse
import org.litote.mastodon.ktor.sdk.client.OperationResponseFailure
import org.litote.mastodon.ktor.sdk.client.OperationResponseSuccess
import org.litote.mastodon.ktor.sdk.client.OperationResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public interface TagsApiV1TagsNameFollowPostClient {
  /**
   * Follow a hashtag
   */
  public suspend fun postTagFollow(name: String): PostTagFollowResponse

  @Serializable
  public sealed class PostTagFollowResponse : OperationResponse<Tag> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class PostTagFollowResponseSuccess(
    override val body: Tag,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFollowResponse(),
      OperationResponseSuccess<Tag> {
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
  public data class PostTagFollowResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFollowResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class PostTagFollowResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFollowResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class PostTagFollowResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagFollowResponse(),
      OperationResponseUnknownFailure
}

public fun TagsApiV1TagsNameFollowPostClient(configuration: ClientConfiguration = defaultClientConfiguration): TagsApiV1TagsNameFollowPostClient = DefaultTagsApiV1TagsNameFollowPostClient(configuration)

public class DefaultTagsApiV1TagsNameFollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TagsApiV1TagsNameFollowPostClient {
  override suspend fun postTagFollow(name: String): TagsApiV1TagsNameFollowPostClient.PostTagFollowResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{name}/follow".replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> TagsApiV1TagsNameFollowPostClient.PostTagFollowResponseSuccess(response.body<Tag>(), response.headers)
        401, 404, 422, 429, 503 -> TagsApiV1TagsNameFollowPostClient.PostTagFollowResponseFailure401(response.body<Error>(), response.headers)
        410 -> TagsApiV1TagsNameFollowPostClient.PostTagFollowResponseFailure(response.headers)
        else -> TagsApiV1TagsNameFollowPostClient.PostTagFollowResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TagsApiV1TagsNameFollowPostClient.PostTagFollowResponseUnknownFailure(500)
    }
  }
}
