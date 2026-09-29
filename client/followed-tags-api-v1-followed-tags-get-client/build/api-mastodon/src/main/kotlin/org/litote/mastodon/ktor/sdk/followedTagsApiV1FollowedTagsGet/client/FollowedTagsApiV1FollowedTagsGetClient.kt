package org.litote.mastodon.ktor.sdk.followedTagsApiV1FollowedTagsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public interface FollowedTagsApiV1FollowedTagsGetClient {
  /**
   * View all followed tags
   */
  public suspend fun getFollowedTags(
    limit: Long? = 100,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetFollowedTagsResponse

  @Serializable
  public sealed class GetFollowedTagsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFollowedTagsResponseSuccess(
    public val body: List<Tag>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowedTagsResponse() {
    /**
     * Pagination links for browsing older or newer results. Format: Link: <https://mastodon.example/api/v1/endpoint?max_id=7163058>; rel="next", <https://mastodon.example/api/v1/endpoint?min_id=7275607>; rel="prev". See [RFC 8288](https://www.rfc-editor.org/rfc/rfc8288) for more information.
     */
    public val link: String?
      get() = headers["Link"]

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
  public data class GetFollowedTagsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowedTagsResponse()

  @Serializable
  public data class GetFollowedTagsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowedTagsResponse()

  @Serializable
  public data class GetFollowedTagsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowedTagsResponse()

  @Serializable
  public data class GetFollowedTagsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFollowedTagsResponse()
}

public fun FollowedTagsApiV1FollowedTagsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FollowedTagsApiV1FollowedTagsGetClient = DefaultFollowedTagsApiV1FollowedTagsGetClient(configuration)

public class DefaultFollowedTagsApiV1FollowedTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FollowedTagsApiV1FollowedTagsGetClient {
  override suspend fun getFollowedTags(
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/followed_tags") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponseSuccess(response.body<List<Tag>>(), response.headers)
        401, 404, 429, 503 -> FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponseFailure401(response.body<Error>(), response.headers)
        410 -> FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponseFailure410(response.headers)
        422 -> FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FollowedTagsApiV1FollowedTagsGetClient.GetFollowedTagsResponseUnknownFailure(500)
    }
  }
}
