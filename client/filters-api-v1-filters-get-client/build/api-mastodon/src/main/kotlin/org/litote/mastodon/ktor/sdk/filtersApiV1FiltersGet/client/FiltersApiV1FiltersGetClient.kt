package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
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
import org.litote.mastodon.ktor.sdk.sharedFiltersapiv1filtersget35506993.model.V1Filter

public interface FiltersApiV1FiltersGetClient {
  /**
   * View your filters
   */
  public suspend fun getFilters(): GetFiltersResponse

  @Serializable
  public sealed class GetFiltersResponse : OperationResponse<V1Filter> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetFiltersResponseSuccess(
    override val body: V1Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersResponse(),
      OperationResponseSuccess<V1Filter> {
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
  public data class GetFiltersResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetFiltersResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetFiltersResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetFiltersResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersResponse(),
      OperationResponseUnknownFailure
}

public fun FiltersApiV1FiltersGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV1FiltersGetClient = DefaultFiltersApiV1FiltersGetClient(configuration)

public class DefaultFiltersApiV1FiltersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV1FiltersGetClient {
  override suspend fun getFilters(): FiltersApiV1FiltersGetClient.GetFiltersResponse {
    try {
      val response = configuration.client.`get`("api/v1/filters") {
      }
      return when (response.status.value) {
        200 -> FiltersApiV1FiltersGetClient.GetFiltersResponseSuccess(response.body<V1Filter>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV1FiltersGetClient.GetFiltersResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV1FiltersGetClient.GetFiltersResponseFailure410(response.headers)
        422 -> FiltersApiV1FiltersGetClient.GetFiltersResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV1FiltersGetClient.GetFiltersResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV1FiltersGetClient.GetFiltersResponseUnknownFailure(500)
    }
  }
}
