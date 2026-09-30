package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidfeaturedtagsgetB2ed9160.model.FeaturedTag

public interface FeaturedTagsApiV1FeaturedTagsPostClient {
  /**
   * Feature a tag
   */
  public suspend fun createFeaturedTag(request: CreateFeaturedTagRequest): CreateFeaturedTagResponse

  @Serializable
  public data class CreateFeaturedTagRequest(
    public val name: String,
  )

  @Serializable
  public sealed class CreateFeaturedTagResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateFeaturedTagResponseSuccess(
    public val body: FeaturedTag,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFeaturedTagResponse() {
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
  public data class CreateFeaturedTagResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFeaturedTagResponse()

  @Serializable
  public data class CreateFeaturedTagResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFeaturedTagResponse()

  @Serializable
  public data class CreateFeaturedTagResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFeaturedTagResponse()
}

public fun FeaturedTagsApiV1FeaturedTagsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): FeaturedTagsApiV1FeaturedTagsPostClient = DefaultFeaturedTagsApiV1FeaturedTagsPostClient(configuration)

public class DefaultFeaturedTagsApiV1FeaturedTagsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FeaturedTagsApiV1FeaturedTagsPostClient {
  override suspend fun createFeaturedTag(request: FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagRequest): FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagResponse {
    try {
      val response = configuration.client.post("api/v1/featured_tags") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagResponseSuccess(response.body<FeaturedTag>(), response.headers)
        401, 404, 422, 429, 503 -> FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagResponseFailure401(response.body<Error>(), response.headers)
        410 -> FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagResponseFailure(response.headers)
        else -> FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FeaturedTagsApiV1FeaturedTagsPostClient.CreateFeaturedTagResponseUnknownFailure(500)
    }
  }
}
