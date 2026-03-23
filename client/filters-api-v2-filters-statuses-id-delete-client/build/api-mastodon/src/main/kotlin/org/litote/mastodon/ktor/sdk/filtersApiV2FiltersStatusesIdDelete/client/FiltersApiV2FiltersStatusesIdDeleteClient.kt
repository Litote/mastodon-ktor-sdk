package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersStatusesIdDelete.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public class FiltersApiV2FiltersStatusesIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove a status from a filter group
   */
  public suspend fun deleteFiltersStatusesByIdV2(id: String): DeleteFiltersStatusesByIdV2Response {
    try {
      val response = configuration.client.delete("api/v2/filters/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteFiltersStatusesByIdV2ResponseSuccess(response.body<FilterStatus>())
        401, 404, 429, 503 -> DeleteFiltersStatusesByIdV2ResponseFailure401(response.body<Error>())
        410 -> DeleteFiltersStatusesByIdV2ResponseFailure410
        422 -> DeleteFiltersStatusesByIdV2ResponseFailure(response.body<ValidationError>())
        else -> DeleteFiltersStatusesByIdV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteFiltersStatusesByIdV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteFiltersStatusesByIdV2Response

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseSuccess(
    public val body: FilterStatus,
  ) : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseFailure401(
    public val body: Error,
  ) : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public object DeleteFiltersStatusesByIdV2ResponseFailure410 : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseFailure(
    public val body: ValidationError,
  ) : DeleteFiltersStatusesByIdV2Response()

  @Serializable
  public data class DeleteFiltersStatusesByIdV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteFiltersStatusesByIdV2Response()
}
