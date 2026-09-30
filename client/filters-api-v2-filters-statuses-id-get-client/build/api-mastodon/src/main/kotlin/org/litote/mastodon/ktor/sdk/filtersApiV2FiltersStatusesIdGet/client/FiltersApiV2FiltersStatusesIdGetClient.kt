package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersStatusesIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public interface FiltersApiV2FiltersStatusesIdGetClient {
  /**
   * View a single status filter
   */
  public suspend fun getFiltersStatusesByIdV2(id: String): GetFiltersStatusesByIdV2Response

  @Serializable
  public sealed class GetFiltersStatusesByIdV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseSuccess(
    public val body: FilterStatus,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersStatusesByIdV2Response() {
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
  public data class GetFiltersStatusesByIdV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersStatusesByIdV2Response()

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersStatusesByIdV2Response()

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersStatusesByIdV2Response()

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersStatusesByIdV2Response()
}

public fun FiltersApiV2FiltersStatusesIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersStatusesIdGetClient = DefaultFiltersApiV2FiltersStatusesIdGetClient(configuration)

public class DefaultFiltersApiV2FiltersStatusesIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersStatusesIdGetClient {
  override suspend fun getFiltersStatusesByIdV2(id: String): FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2ResponseSuccess(response.body<FilterStatus>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersStatusesIdGetClient.GetFiltersStatusesByIdV2ResponseUnknownFailure(500)
    }
  }
}
