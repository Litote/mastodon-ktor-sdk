package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum
import org.litote.mastodon.ktor.sdk.sharedFiltersapiv1filtersget35506993.model.V1Filter

public interface FiltersApiV1FiltersIdPutClient {
  /**
   * Update a filter
   */
  public suspend fun updateFilter(request: UpdateFilterRequest, id: String): UpdateFilterResponse

  @Serializable
  public data class UpdateFilterRequest(
    public val context: List<FilterContextEnum>,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    public val irreversible: Boolean? = false,
    public val phrase: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = false,
  )

  @Serializable
  public sealed class UpdateFilterResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateFilterResponseSuccess(
    public val body: V1Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterResponse() {
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
  public data class UpdateFilterResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterResponse()

  @Serializable
  public data class UpdateFilterResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterResponse()

  @Serializable
  public data class UpdateFilterResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterResponse()
}

public fun FiltersApiV1FiltersIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV1FiltersIdPutClient = DefaultFiltersApiV1FiltersIdPutClient(configuration)

public class DefaultFiltersApiV1FiltersIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV1FiltersIdPutClient {
  override suspend fun updateFilter(request: FiltersApiV1FiltersIdPutClient.UpdateFilterRequest, id: String): FiltersApiV1FiltersIdPutClient.UpdateFilterResponse {
    try {
      val response = configuration.client.put("api/v1/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FiltersApiV1FiltersIdPutClient.UpdateFilterResponseSuccess(response.body<V1Filter>(), response.headers)
        401, 404, 422, 429, 503 -> FiltersApiV1FiltersIdPutClient.UpdateFilterResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV1FiltersIdPutClient.UpdateFilterResponseFailure(response.headers)
        else -> FiltersApiV1FiltersIdPutClient.UpdateFilterResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV1FiltersIdPutClient.UpdateFilterResponseUnknownFailure(500)
    }
  }
}
