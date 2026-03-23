package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersFilterIdKeywordsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public class FiltersApiV2FiltersFilterIdKeywordsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View keywords added to a filter
   */
  public suspend fun getFilterKeywordsV2(filterId: String): GetFilterKeywordsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/{filter_id}/keywords".replace("/{filter_id}", "/${filterId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetFilterKeywordsV2ResponseSuccess(response.body<List<FilterKeyword>>())
        401, 404, 429, 503 -> GetFilterKeywordsV2ResponseFailure401(response.body<Error>())
        410 -> GetFilterKeywordsV2ResponseFailure410
        422 -> GetFilterKeywordsV2ResponseFailure(response.body<ValidationError>())
        else -> GetFilterKeywordsV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFilterKeywordsV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFilterKeywordsV2Response

  @Serializable
  public data class GetFilterKeywordsV2ResponseSuccess(
    public val body: List<FilterKeyword>,
  ) : GetFilterKeywordsV2Response()

  @Serializable
  public data class GetFilterKeywordsV2ResponseFailure401(
    public val body: Error,
  ) : GetFilterKeywordsV2Response()

  @Serializable
  public object GetFilterKeywordsV2ResponseFailure410 : GetFilterKeywordsV2Response()

  @Serializable
  public data class GetFilterKeywordsV2ResponseFailure(
    public val body: ValidationError,
  ) : GetFilterKeywordsV2Response()

  @Serializable
  public data class GetFilterKeywordsV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFilterKeywordsV2Response()
}
