package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter

public interface FiltersApiV2FiltersIdGetClient {
  /**
   * View a specific filter
   */
  public suspend fun getFilterV2(id: String): GetFilterV2Response

  @Serializable
  public sealed class GetFilterV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFilterV2ResponseSuccess(
    public val body: Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterV2Response() {
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
  public data class GetFilterV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterV2Response()

  @Serializable
  public data class GetFilterV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterV2Response()

  @Serializable
  public data class GetFilterV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterV2Response()

  @Serializable
  public data class GetFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterV2Response()
}

public fun FiltersApiV2FiltersIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersIdGetClient = DefaultFiltersApiV2FiltersIdGetClient(configuration)

public class DefaultFiltersApiV2FiltersIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersIdGetClient {
  override suspend fun getFilterV2(id: String): FiltersApiV2FiltersIdGetClient.GetFilterV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersIdGetClient.GetFilterV2ResponseSuccess(response.body<Filter>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersIdGetClient.GetFilterV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersIdGetClient.GetFilterV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersIdGetClient.GetFilterV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersIdGetClient.GetFilterV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersIdGetClient.GetFilterV2ResponseUnknownFailure(500)
    }
  }
}
