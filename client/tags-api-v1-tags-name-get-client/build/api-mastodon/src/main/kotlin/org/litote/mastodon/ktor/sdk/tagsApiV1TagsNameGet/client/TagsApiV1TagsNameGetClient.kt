package org.litote.mastodon.ktor.sdk.tagsApiV1TagsNameGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
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

public interface TagsApiV1TagsNameGetClient {
  /**
   * View information about a single tag
   */
  public suspend fun getTagsByName(name: String): GetTagsByNameResponse

  @Serializable
  public sealed class GetTagsByNameResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetTagsByNameResponseSuccess(
    public val body: Tag,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTagsByNameResponse() {
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
  public data class GetTagsByNameResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTagsByNameResponse()

  @Serializable
  public data class GetTagsByNameResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTagsByNameResponse()

  @Serializable
  public data class GetTagsByNameResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTagsByNameResponse()

  @Serializable
  public data class GetTagsByNameResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetTagsByNameResponse()
}

public fun TagsApiV1TagsNameGetClient(configuration: ClientConfiguration = defaultClientConfiguration): TagsApiV1TagsNameGetClient = DefaultTagsApiV1TagsNameGetClient(configuration)

public class DefaultTagsApiV1TagsNameGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : TagsApiV1TagsNameGetClient {
  override suspend fun getTagsByName(name: String): TagsApiV1TagsNameGetClient.GetTagsByNameResponse {
    try {
      val response = configuration.client.`get`("api/v1/tags/{name}".replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> TagsApiV1TagsNameGetClient.GetTagsByNameResponseSuccess(response.body<Tag>(), response.headers)
        401, 404, 429, 503 -> TagsApiV1TagsNameGetClient.GetTagsByNameResponseFailure401(response.body<Error>(), response.headers)
        410 -> TagsApiV1TagsNameGetClient.GetTagsByNameResponseFailure410(response.headers)
        422 -> TagsApiV1TagsNameGetClient.GetTagsByNameResponseFailure(response.body<ValidationError>(), response.headers)
        else -> TagsApiV1TagsNameGetClient.GetTagsByNameResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return TagsApiV1TagsNameGetClient.GetTagsByNameResponseUnknownFailure(500)
    }
  }
}
