package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersKeywordsIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class FiltersApiV2FiltersKeywordsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove keywords from a filter
   */
  public suspend fun deleteFiltersKeywordsByIdV2(id: String): DeleteFiltersKeywordsByIdV2Response {
    try {
      val response = configuration.client.delete("api/v2/filters/keywords/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteFiltersKeywordsByIdV2ResponseSuccess
        401, 404, 429, 503 -> DeleteFiltersKeywordsByIdV2ResponseFailure401(response.body<Error>())
        410 -> DeleteFiltersKeywordsByIdV2ResponseFailure410
        422 -> DeleteFiltersKeywordsByIdV2ResponseFailure(response.body<ValidationError>())
        else -> DeleteFiltersKeywordsByIdV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteFiltersKeywordsByIdV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteFiltersKeywordsByIdV2Response

  @Serializable
  public object DeleteFiltersKeywordsByIdV2ResponseSuccess : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseFailure401(
    public val body: Error,
  ) : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public object DeleteFiltersKeywordsByIdV2ResponseFailure410 : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseFailure(
    public val body: ValidationError,
  ) : DeleteFiltersKeywordsByIdV2Response()

  @Serializable
  public data class DeleteFiltersKeywordsByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteFiltersKeywordsByIdV2Response()
}
