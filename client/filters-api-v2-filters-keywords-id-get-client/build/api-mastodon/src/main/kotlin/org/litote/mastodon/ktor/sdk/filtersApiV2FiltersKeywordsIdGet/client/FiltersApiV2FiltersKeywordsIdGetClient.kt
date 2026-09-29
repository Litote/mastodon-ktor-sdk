package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersKeywordsIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public interface FiltersApiV2FiltersKeywordsIdGetClient {
  /**
   * View a single keyword
   */
  public suspend fun getFiltersKeywordsByIdV2(id: String): GetFiltersKeywordsByIdV2Response

  @Serializable
  public sealed class GetFiltersKeywordsByIdV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseSuccess(
    public val body: FilterKeyword,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersKeywordsByIdV2Response() {
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
  public data class GetFiltersKeywordsByIdV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetFiltersKeywordsByIdV2Response()
}

public fun FiltersApiV2FiltersKeywordsIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersKeywordsIdGetClient = DefaultFiltersApiV2FiltersKeywordsIdGetClient(configuration)

public class DefaultFiltersApiV2FiltersKeywordsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersKeywordsIdGetClient {
  override suspend fun getFiltersKeywordsByIdV2(id: String): FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/keywords/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2ResponseSuccess(response.body<FilterKeyword>(), response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersKeywordsIdGetClient.GetFiltersKeywordsByIdV2ResponseUnknownFailure(500)
    }
  }
}
