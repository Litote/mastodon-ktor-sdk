package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersStatusesIdDelete.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public interface FiltersApiV2FiltersStatusesIdDeleteClient {
  /**
   * Remove a status from a filter group
   */
  public suspend fun deleteFiltersStatusesByIdV2(id: String): DeleteFiltersStatusesByIdV2Response

  @Serializable
  public sealed class DeleteFiltersStatusesByIdV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseSuccess(
    public val body: FilterStatus,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersStatusesByIdV2Response() {
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
  public data class DeleteFiltersStatusesByIdV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersStatusesByIdV2Response()
}

public fun FiltersApiV2FiltersStatusesIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersStatusesIdDeleteClient = DefaultFiltersApiV2FiltersStatusesIdDeleteClient(configuration)

public class DefaultFiltersApiV2FiltersStatusesIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersStatusesIdDeleteClient {
  override suspend fun deleteFiltersStatusesByIdV2(id: String): FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2Response {
    try {
      val response = configuration.client.delete("api/v2/filters/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2ResponseSuccess(response.body<FilterStatus>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersStatusesIdDeleteClient.DeleteFiltersStatusesByIdV2ResponseUnknownFailure(500)
    }
  }
}
