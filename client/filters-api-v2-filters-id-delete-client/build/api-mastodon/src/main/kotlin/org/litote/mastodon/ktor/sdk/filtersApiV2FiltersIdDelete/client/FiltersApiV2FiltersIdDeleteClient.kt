package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersIdDelete.client

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

public interface FiltersApiV2FiltersIdDeleteClient {
  /**
   * Delete a filter
   */
  public suspend fun deleteFilterV2(id: String): DeleteFilterV2Response

  @Serializable
  public sealed class DeleteFilterV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteFilterV2ResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterV2Response() {
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
  public data class DeleteFilterV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterV2Response()

  @Serializable
  public data class DeleteFilterV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterV2Response()

  @Serializable
  public data class DeleteFilterV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterV2Response()

  @Serializable
  public data class DeleteFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterV2Response()
}

public fun FiltersApiV2FiltersIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersIdDeleteClient = DefaultFiltersApiV2FiltersIdDeleteClient(configuration)

public class DefaultFiltersApiV2FiltersIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersIdDeleteClient {
  override suspend fun deleteFilterV2(id: String): FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2Response {
    try {
      val response = configuration.client.delete("api/v2/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2ResponseSuccess(response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersIdDeleteClient.DeleteFilterV2ResponseUnknownFailure(500)
    }
  }
}
