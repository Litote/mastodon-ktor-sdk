package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersKeywordsIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public interface FiltersApiV2FiltersKeywordsIdPutClient {
  /**
   * Edit a keyword within a filter
   */
  public suspend fun updateFiltersKeywordsByIdV2(request: UpdateFiltersKeywordsByIdV2Request, id: String): UpdateFiltersKeywordsByIdV2Response

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2Request(
    public val keyword: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = null,
  )

  @Serializable
  public sealed class UpdateFiltersKeywordsByIdV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2ResponseSuccess(
    public val body: FilterKeyword,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFiltersKeywordsByIdV2Response() {
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
  public data class UpdateFiltersKeywordsByIdV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFiltersKeywordsByIdV2Response()

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2ResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFiltersKeywordsByIdV2Response()

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateFiltersKeywordsByIdV2Response()
}

public fun FiltersApiV2FiltersKeywordsIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersKeywordsIdPutClient = DefaultFiltersApiV2FiltersKeywordsIdPutClient(configuration)

public class DefaultFiltersApiV2FiltersKeywordsIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersKeywordsIdPutClient {
  override suspend fun updateFiltersKeywordsByIdV2(request: FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2Request, id: String): FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2Response {
    try {
      val response = configuration.client.put("api/v2/filters/keywords/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2ResponseSuccess(response.body<FilterKeyword>(), response.headers)
        401, 404, 422, 429, 503 -> FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2ResponseFailure(response.headers)
        else -> FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersKeywordsIdPutClient.UpdateFiltersKeywordsByIdV2ResponseUnknownFailure(500)
    }
  }
}
