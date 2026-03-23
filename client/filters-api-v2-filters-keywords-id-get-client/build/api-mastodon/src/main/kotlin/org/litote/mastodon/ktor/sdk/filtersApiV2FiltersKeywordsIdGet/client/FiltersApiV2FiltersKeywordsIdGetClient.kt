package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersKeywordsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public class FiltersApiV2FiltersKeywordsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a single keyword
   */
  public suspend fun getFiltersKeywordsByIdV2(id: String): GetFiltersKeywordsByIdV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/keywords/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetFiltersKeywordsByIdV2ResponseSuccess(response.body<FilterKeyword>())
        401, 404, 429, 503 -> GetFiltersKeywordsByIdV2ResponseFailure401(response.body<Error>())
        410 -> GetFiltersKeywordsByIdV2ResponseFailure410
        422 -> GetFiltersKeywordsByIdV2ResponseFailure(response.body<ValidationError>())
        else -> GetFiltersKeywordsByIdV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFiltersKeywordsByIdV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFiltersKeywordsByIdV2Response

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseSuccess(
    public val body: FilterKeyword,
  ) : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseFailure401(
    public val body: Error,
  ) : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public object GetFiltersKeywordsByIdV2ResponseFailure410 : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseFailure(
    public val body: ValidationError,
  ) : GetFiltersKeywordsByIdV2Response()

  @Serializable
  public data class GetFiltersKeywordsByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFiltersKeywordsByIdV2Response()
}
