package org.litote.mastodon.ktor.sdk.tagsApiV1TagsNameUnfollowPost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public interface TagsApiV1TagsNameUnfollowPostClient {
  /**
   * Unfollow a hashtag
   */
  public suspend fun postTagUnfollow(name: String): PostTagUnfollowResponse

  @Serializable
  public sealed class PostTagUnfollowResponse : OperationResponse<Tag> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class PostTagUnfollowResponseSuccess(
    override val body: Tag,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfollowResponse(),
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
  public data class PostTagUnfollowResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfollowResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class PostTagUnfollowResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfollowResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class PostTagUnfollowResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfollowResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class PostTagUnfollowResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostTagUnfollowResponse(),
      OperationResponseUnknownFailure
}

public fun TagsApiV1TagsNameUnfollowPostClient(configuration: ClientConfiguration = defaultClientConfiguration): TagsApiV1TagsNameUnfollowPostClient = DefaultTagsApiV1TagsNameUnfollowPostClient(configuration)

public class DefaultTagsApiV1TagsNameUnfollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TagsApiV1TagsNameUnfollowPostClient {
  override suspend fun postTagUnfollow(name: String): TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponse {
    try {
      val response = configuration.client.post("api/v1/tags/{name}/unfollow".replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponseSuccess(response.body<Tag>(), response.headers)
        401, 404, 429, 503 -> TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponseFailure401(response.body<Error>(), response.headers)
        410 -> TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponseFailure410(response.headers)
        422 -> TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TagsApiV1TagsNameUnfollowPostClient.PostTagUnfollowResponseUnknownFailure(500)
    }
  }
}
