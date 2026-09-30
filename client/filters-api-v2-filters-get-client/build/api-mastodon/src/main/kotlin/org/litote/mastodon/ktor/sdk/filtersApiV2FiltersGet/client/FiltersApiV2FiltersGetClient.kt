package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter

public interface FiltersApiV2FiltersGetClient {
  /**
   * View all filters
   */
  public suspend fun getFiltersV2(): GetFiltersV2Response

  @Serializable
  public sealed class GetFiltersV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFiltersV2ResponseSuccess(
    public val body: List<Filter>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersV2Response() {
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
  public data class GetFiltersV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersV2Response()

  @Serializable
  public data class GetFiltersV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersV2Response()

  @Serializable
  public data class GetFiltersV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersV2Response()

  @Serializable
  public data class GetFiltersV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersV2Response()
}

public fun FiltersApiV2FiltersGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersGetClient = DefaultFiltersApiV2FiltersGetClient(configuration)

public class DefaultFiltersApiV2FiltersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersGetClient {
  override suspend fun getFiltersV2(): FiltersApiV2FiltersGetClient.GetFiltersV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters") {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersGetClient.GetFiltersV2ResponseSuccess(response.body<List<Filter>>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersGetClient.GetFiltersV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersGetClient.GetFiltersV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersGetClient.GetFiltersV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersGetClient.GetFiltersV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersGetClient.GetFiltersV2ResponseUnknownFailure(500)
    }
  }
}
