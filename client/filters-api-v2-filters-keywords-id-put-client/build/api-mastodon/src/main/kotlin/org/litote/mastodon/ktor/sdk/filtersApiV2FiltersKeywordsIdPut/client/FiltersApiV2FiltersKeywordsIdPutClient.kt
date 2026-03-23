package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersKeywordsIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public class FiltersApiV2FiltersKeywordsIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Edit a keyword within a filter
   */
  public suspend fun updateFiltersKeywordsByIdV2(request: UpdateFiltersKeywordsByIdV2Request, id: String): UpdateFiltersKeywordsByIdV2Response {
    try {
      val response = configuration.client.put("api/v2/filters/keywords/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateFiltersKeywordsByIdV2ResponseSuccess(response.body<FilterKeyword>())
        401, 404, 422, 429, 503 -> UpdateFiltersKeywordsByIdV2ResponseFailure401(response.body<Error>())
        410 -> UpdateFiltersKeywordsByIdV2ResponseFailure
        else -> UpdateFiltersKeywordsByIdV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateFiltersKeywordsByIdV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2Request(
    public val keyword: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = null,
  )

  @Serializable
  public sealed class UpdateFiltersKeywordsByIdV2Response

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2ResponseSuccess(
    public val body: FilterKeyword,
  ) : UpdateFiltersKeywordsByIdV2Response()

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2ResponseFailure401(
    public val body: Error,
  ) : UpdateFiltersKeywordsByIdV2Response()

  @Serializable
  public object UpdateFiltersKeywordsByIdV2ResponseFailure : UpdateFiltersKeywordsByIdV2Response()

  @Serializable
  public data class UpdateFiltersKeywordsByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateFiltersKeywordsByIdV2Response()
}
