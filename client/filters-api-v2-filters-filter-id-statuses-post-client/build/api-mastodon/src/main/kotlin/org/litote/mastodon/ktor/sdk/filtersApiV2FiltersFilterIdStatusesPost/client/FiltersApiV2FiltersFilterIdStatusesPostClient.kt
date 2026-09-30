package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersFilterIdStatusesPost.client

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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public interface FiltersApiV2FiltersFilterIdStatusesPostClient {
  /**
   * Add a status to a filter group
   */
  public suspend fun postFilterStatusesV2(request: PostFilterStatusesV2Request, filterId: String): PostFilterStatusesV2Response

  @Serializable
  public data class PostFilterStatusesV2Request(
    @SerialName("status_id")
    public val statusId: String,
  )

  @Serializable
  public sealed class PostFilterStatusesV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostFilterStatusesV2ResponseSuccess(
    public val body: FilterStatus,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFilterStatusesV2Response() {
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
  public data class PostFilterStatusesV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFilterStatusesV2Response()

  @Serializable
  public data class PostFilterStatusesV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFilterStatusesV2Response()

  @Serializable
  public data class PostFilterStatusesV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFilterStatusesV2Response()

  @Serializable
  public data class PostFilterStatusesV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostFilterStatusesV2Response()
}

public fun FiltersApiV2FiltersFilterIdStatusesPostClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersFilterIdStatusesPostClient = DefaultFiltersApiV2FiltersFilterIdStatusesPostClient(configuration)

public class DefaultFiltersApiV2FiltersFilterIdStatusesPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersFilterIdStatusesPostClient {
  override suspend fun postFilterStatusesV2(request: FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2Request, filterId: String): FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2Response {
    try {
      val response = configuration.client.post("api/v2/filters/{filter_id}/statuses".replace("/{filter_id}", "/${filterId.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2ResponseSuccess(response.body<FilterStatus>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersFilterIdStatusesPostClient.PostFilterStatusesV2ResponseUnknownFailure(500)
    }
  }
}
