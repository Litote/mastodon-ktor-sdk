package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum

public interface FiltersApiV2FiltersPostClient {
  /**
   * Create a filter
   */
  public suspend fun createFilterV2(request: CreateFilterV2Request): CreateFilterV2Response

  @Serializable
  public data class CreateFilterV2Request(
    public val context: List<FilterContextEnum>,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    @SerialName("filter_action")
    public val filterAction: String? = null,
    @SerialName("keywords_attributes")
    public val keywordsAttributes: List<JsonElement>? = null,
    public val title: String,
  )

  @Serializable
  public sealed class CreateFilterV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateFilterV2ResponseSuccess(
    public val body: Filter,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterV2Response() {
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
  public data class CreateFilterV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterV2Response()

  @Serializable
  public data class CreateFilterV2ResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterV2Response()

  @Serializable
  public data class CreateFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateFilterV2Response()
}

public fun FiltersApiV2FiltersPostClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersPostClient = DefaultFiltersApiV2FiltersPostClient(configuration)

public class DefaultFiltersApiV2FiltersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersPostClient {
  override suspend fun createFilterV2(request: FiltersApiV2FiltersPostClient.CreateFilterV2Request): FiltersApiV2FiltersPostClient.CreateFilterV2Response {
    try {
      val response = configuration.client.post("api/v2/filters") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersPostClient.CreateFilterV2ResponseSuccess(response.body<Filter>(), response.headers)
        401, 404, 422, 429, 503 -> FiltersApiV2FiltersPostClient.CreateFilterV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersPostClient.CreateFilterV2ResponseFailure(response.headers)
        else -> FiltersApiV2FiltersPostClient.CreateFilterV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersPostClient.CreateFilterV2ResponseUnknownFailure(500)
    }
  }
}
