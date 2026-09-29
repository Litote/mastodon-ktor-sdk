package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
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

public interface FiltersApiV1FiltersPostClient {
  /**
   * Create a filter
   */
  public suspend fun createFilter(request: CreateFilterRequest): CreateFilterResponse

  @Serializable
  public data class CreateFilterRequest(
    public val context: List<FilterContextEnum>,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    public val irreversible: Boolean? = false,
    public val phrase: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = false,
  )

  @Serializable
  public sealed class CreateFilterResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateFilterResponseSuccess(
    public val body: V1Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterResponse() {
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
  public data class CreateFilterResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterResponse()

  @Serializable
  public data class CreateFilterResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterResponse()

  @Serializable
  public data class CreateFilterResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterResponse()
}

public fun FiltersApiV1FiltersPostClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV1FiltersPostClient = DefaultFiltersApiV1FiltersPostClient(configuration)

public class DefaultFiltersApiV1FiltersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV1FiltersPostClient {
  override suspend fun createFilter(request: FiltersApiV1FiltersPostClient.CreateFilterRequest): FiltersApiV1FiltersPostClient.CreateFilterResponse {
    try {
      val response = configuration.client.post("api/v1/filters") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FiltersApiV1FiltersPostClient.CreateFilterResponseSuccess(response.body<V1Filter>(), response.headers)
        401, 404, 422, 429, 503 -> FiltersApiV1FiltersPostClient.CreateFilterResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV1FiltersPostClient.CreateFilterResponseFailure(response.headers)
        else -> FiltersApiV1FiltersPostClient.CreateFilterResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV1FiltersPostClient.CreateFilterResponseUnknownFailure(500)
    }
  }
}
