package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersStatusesIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public class FiltersApiV2FiltersStatusesIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a single status filter
   */
  public suspend fun getFiltersStatusesByIdV2(id: String): GetFiltersStatusesByIdV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetFiltersStatusesByIdV2ResponseSuccess(response.body<FilterStatus>())
        401, 404, 429, 503 -> GetFiltersStatusesByIdV2ResponseFailure401(response.body<Error>())
        410 -> GetFiltersStatusesByIdV2ResponseFailure410
        422 -> GetFiltersStatusesByIdV2ResponseFailure(response.body<ValidationError>())
        else -> GetFiltersStatusesByIdV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFiltersStatusesByIdV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFiltersStatusesByIdV2Response

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseSuccess(
    public val body: FilterStatus,
  ) : GetFiltersStatusesByIdV2Response()

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseFailure401(
    public val body: Error,
  ) : GetFiltersStatusesByIdV2Response()

  @Serializable
  public object GetFiltersStatusesByIdV2ResponseFailure410 : GetFiltersStatusesByIdV2Response()

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseFailure(
    public val body: ValidationError,
  ) : GetFiltersStatusesByIdV2Response()

  @Serializable
  public data class GetFiltersStatusesByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFiltersStatusesByIdV2Response()
}
