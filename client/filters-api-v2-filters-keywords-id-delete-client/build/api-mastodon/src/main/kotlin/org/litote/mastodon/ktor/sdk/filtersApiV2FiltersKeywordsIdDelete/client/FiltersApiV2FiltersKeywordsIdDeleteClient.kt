package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersKeywordsIdDelete.client

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

public interface FiltersApiV2FiltersKeywordsIdDeleteClient {
  /**
   * Remove keywords from a filter
   */
  public suspend fun deleteFiltersKeywordsByIdV2(id: String): DeleteFiltersKeywordsByIdV2Response

  @Serializable
  public sealed class DeleteFiltersKeywordsByIdV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersKeywordsByIdV2Response() {
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
  public data class DeleteFiltersKeywordsByIdV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteFiltersKeywordsByIdV2Response()
}

public fun FiltersApiV2FiltersKeywordsIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): FiltersApiV2FiltersKeywordsIdDeleteClient = DefaultFiltersApiV2FiltersKeywordsIdDeleteClient(configuration)

public class DefaultFiltersApiV2FiltersKeywordsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : FiltersApiV2FiltersKeywordsIdDeleteClient {
  override suspend fun deleteFiltersKeywordsByIdV2(id: String): FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2Response {
    try {
      val response = configuration.client.delete("api/v2/filters/keywords/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2ResponseSuccess(response.headers)
        401, 404, 429, 503 -> FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2ResponseFailure410(response.headers)
        422 -> FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return FiltersApiV2FiltersKeywordsIdDeleteClient.DeleteFiltersKeywordsByIdV2ResponseUnknownFailure(500)
    }
  }
}
