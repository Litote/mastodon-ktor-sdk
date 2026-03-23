package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersFilterIdStatusesGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public class FiltersApiV2FiltersFilterIdStatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View all status filters
   */
  public suspend fun getFilterStatusesV2(filterId: String): GetFilterStatusesV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/{filter_id}/statuses".replace("/{filter_id}", "/${filterId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetFilterStatusesV2ResponseSuccess(response.body<List<FilterStatus>>())
        401, 404, 429, 503 -> GetFilterStatusesV2ResponseFailure401(response.body<Error>())
        410 -> GetFilterStatusesV2ResponseFailure410
        422 -> GetFilterStatusesV2ResponseFailure(response.body<ValidationError>())
        else -> GetFilterStatusesV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFilterStatusesV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFilterStatusesV2Response

  @Serializable
  public data class GetFilterStatusesV2ResponseSuccess(
    public val body: List<FilterStatus>,
  ) : GetFilterStatusesV2Response()

  @Serializable
  public data class GetFilterStatusesV2ResponseFailure401(
    public val body: Error,
  ) : GetFilterStatusesV2Response()

  @Serializable
  public object GetFilterStatusesV2ResponseFailure410 : GetFilterStatusesV2Response()

  @Serializable
  public data class GetFilterStatusesV2ResponseFailure(
    public val body: ValidationError,
  ) : GetFilterStatusesV2Response()

  @Serializable
  public data class GetFilterStatusesV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFilterStatusesV2Response()
}
