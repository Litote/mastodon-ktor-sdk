package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum

public interface FiltersApiV2FiltersIdPutClient {
  /**
   * Update a filter
   */
  public suspend fun updateFilterV2(request: UpdateFilterV2Request, id: String): UpdateFilterV2Response

  @Serializable
  public data class UpdateFilterV2Request(
    public val context: List<FilterContextEnum>? = null,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    @SerialName("filter_action")
    public val filterAction: String? = null,
    @SerialName("keywords_attributes")
    public val keywordsAttributes: List<JsonElement>? = null,
    public val title: String? = null,
  )

  @Serializable
  public sealed class UpdateFilterV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateFilterV2ResponseSuccess(
    public val body: Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterV2Response() {
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
  public data class UpdateFilterV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterV2Response()

  @Serializable
  public data class UpdateFilterV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterV2Response()

  @Serializable
  public data class UpdateFilterV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterV2Response()

  @Serializable
  public data class UpdateFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFilterV2Response()
}

public fun FiltersApiV2FiltersIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersIdPutClient = DefaultFiltersApiV2FiltersIdPutClient(configuration)

public class DefaultFiltersApiV2FiltersIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersIdPutClient {
  override suspend fun updateFilterV2(request: FiltersApiV2FiltersIdPutClient.UpdateFilterV2Request, id: String): FiltersApiV2FiltersIdPutClient.UpdateFilterV2Response {
    try {
      val response = configuration.client.put("api/v2/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersIdPutClient.UpdateFilterV2ResponseSuccess(response.body<Filter>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersIdPutClient.UpdateFilterV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersIdPutClient.UpdateFilterV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersIdPutClient.UpdateFilterV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersIdPutClient.UpdateFilterV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersIdPutClient.UpdateFilterV2ResponseUnknownFailure(500)
    }
  }
}
