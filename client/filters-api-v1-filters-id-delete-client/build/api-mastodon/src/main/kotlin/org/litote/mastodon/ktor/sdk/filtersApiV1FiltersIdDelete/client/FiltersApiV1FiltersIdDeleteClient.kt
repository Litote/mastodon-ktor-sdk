package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
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

public interface FiltersApiV1FiltersIdDeleteClient {
  /**
   * Remove a filter
   */
  public suspend fun deleteFilter(id: String): DeleteFilterResponse

  @Serializable
  public sealed class DeleteFilterResponse : OperationResponse<Unit> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class DeleteFilterResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterResponse(),
      OperationResponseSuccess<Unit> {
    override val body: Unit
      get() = Unit

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
  public data class DeleteFilterResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class DeleteFilterResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class DeleteFilterResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class DeleteFilterResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFilterResponse(),
      OperationResponseUnknownFailure
}

public fun FiltersApiV1FiltersIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV1FiltersIdDeleteClient = DefaultFiltersApiV1FiltersIdDeleteClient(configuration)

public class DefaultFiltersApiV1FiltersIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV1FiltersIdDeleteClient {
  override suspend fun deleteFilter(id: String): FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponse {
    try {
      val response = configuration.client.delete("api/v1/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponseSuccess(response.headers)
        401, 404, 429, 503 -> FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponseFailure410(response.headers)
        422 -> FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV1FiltersIdDeleteClient.DeleteFilterResponseUnknownFailure(500)
    }
  }
}
