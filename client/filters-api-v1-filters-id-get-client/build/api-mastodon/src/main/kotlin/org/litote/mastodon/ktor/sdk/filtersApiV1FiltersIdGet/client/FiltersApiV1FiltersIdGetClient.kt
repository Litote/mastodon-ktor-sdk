package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedFiltersapiv1filtersget35506993.model.V1Filter

public interface FiltersApiV1FiltersIdGetClient {
  /**
   * View a single filter
   */
  public suspend fun getFilter(id: String): GetFilterResponse

  @Serializable
  public sealed class GetFilterResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFilterResponseSuccess(
    public val body: V1Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterResponse() {
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
  public data class GetFilterResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterResponse()

  @Serializable
  public data class GetFilterResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterResponse()

  @Serializable
  public data class GetFilterResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterResponse()

  @Serializable
  public data class GetFilterResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterResponse()
}

public fun FiltersApiV1FiltersIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV1FiltersIdGetClient = DefaultFiltersApiV1FiltersIdGetClient(configuration)

public class DefaultFiltersApiV1FiltersIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV1FiltersIdGetClient {
  override suspend fun getFilter(id: String): FiltersApiV1FiltersIdGetClient.GetFilterResponse {
    try {
      val response = configuration.client.`get`("api/v1/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV1FiltersIdGetClient.GetFilterResponseSuccess(response.body<V1Filter>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV1FiltersIdGetClient.GetFilterResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV1FiltersIdGetClient.GetFilterResponseFailure410(response.headers)
        422 -> FiltersApiV1FiltersIdGetClient.GetFilterResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV1FiltersIdGetClient.GetFilterResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV1FiltersIdGetClient.GetFilterResponseUnknownFailure(500)
    }
  }
}
