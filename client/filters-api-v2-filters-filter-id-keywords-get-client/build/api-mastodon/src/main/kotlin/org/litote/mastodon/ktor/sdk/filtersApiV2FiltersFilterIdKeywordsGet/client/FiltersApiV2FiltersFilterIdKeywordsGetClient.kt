package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersFilterIdKeywordsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public interface FiltersApiV2FiltersFilterIdKeywordsGetClient {
  /**
   * View keywords added to a filter
   */
  public suspend fun getFilterKeywordsV2(filterId: String): GetFilterKeywordsV2Response

  @Serializable
  public sealed class GetFilterKeywordsV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFilterKeywordsV2ResponseSuccess(
    public val body: List<FilterKeyword>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterKeywordsV2Response() {
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
  public data class GetFilterKeywordsV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterKeywordsV2Response()

  @Serializable
  public data class GetFilterKeywordsV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterKeywordsV2Response()

  @Serializable
  public data class GetFilterKeywordsV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterKeywordsV2Response()

  @Serializable
  public data class GetFilterKeywordsV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFilterKeywordsV2Response()
}

public fun FiltersApiV2FiltersFilterIdKeywordsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersFilterIdKeywordsGetClient = DefaultFiltersApiV2FiltersFilterIdKeywordsGetClient(configuration)

public class DefaultFiltersApiV2FiltersFilterIdKeywordsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersFilterIdKeywordsGetClient {
  override suspend fun getFilterKeywordsV2(filterId: String): FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/{filter_id}/keywords".replace("/{filter_id}", "/${filterId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2ResponseSuccess(response.body<List<FilterKeyword>>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersFilterIdKeywordsGetClient.GetFilterKeywordsV2ResponseUnknownFailure(500)
    }
  }
}
