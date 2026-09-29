package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
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

public interface FeaturedTagsApiV1FeaturedTagsIdDeleteClient {
  /**
   * Unfeature a tag
   */
  public suspend fun deleteFeaturedTag(id: String): DeleteFeaturedTagResponse

  @Serializable
  public sealed class DeleteFeaturedTagResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteFeaturedTagResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFeaturedTagResponse() {
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
  public data class DeleteFeaturedTagResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFeaturedTagResponse()

  @Serializable
  public data class DeleteFeaturedTagResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFeaturedTagResponse()

  @Serializable
  public data class DeleteFeaturedTagResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFeaturedTagResponse()

  @Serializable
  public data class DeleteFeaturedTagResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFeaturedTagResponse()
}

public fun FeaturedTagsApiV1FeaturedTagsIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): FeaturedTagsApiV1FeaturedTagsIdDeleteClient = DefaultFeaturedTagsApiV1FeaturedTagsIdDeleteClient(configuration)

public class DefaultFeaturedTagsApiV1FeaturedTagsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FeaturedTagsApiV1FeaturedTagsIdDeleteClient {
  override suspend fun deleteFeaturedTag(id: String): FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponse {
    try {
      val response = configuration.client.delete("api/v1/featured_tags/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponseSuccess(response.headers)
        401, 404, 429, 503 -> FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponseFailure401(response.body<Error>(), response.headers)
        410 -> FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponseFailure410(response.headers)
        422 -> FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FeaturedTagsApiV1FeaturedTagsIdDeleteClient.DeleteFeaturedTagResponseUnknownFailure(500)
    }
  }
}
