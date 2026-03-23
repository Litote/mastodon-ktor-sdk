package org.litote.mastodon.ktor.sdk.trendsApiV1TrendsStatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class TrendsApiV1TrendsStatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View trending statuses
   */
  public suspend fun getTrendStatuses(limit: Long? = 20, offset: Long? = null): GetTrendStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/trends/statuses") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (offset != null) {
            parameters.append("offset", offset.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> GetTrendStatusesResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetTrendStatusesResponseFailure401(response.body<Error>())
        410 -> GetTrendStatusesResponseFailure410
        422 -> GetTrendStatusesResponseFailure(response.body<ValidationError>())
        else -> GetTrendStatusesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTrendStatusesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTrendStatusesResponse

  @Serializable
  public data class GetTrendStatusesResponseSuccess(
    public val body: List<Status>,
  ) : GetTrendStatusesResponse()

  @Serializable
  public data class GetTrendStatusesResponseFailure401(
    public val body: Error,
  ) : GetTrendStatusesResponse()

  @Serializable
  public object GetTrendStatusesResponseFailure410 : GetTrendStatusesResponse()

  @Serializable
  public data class GetTrendStatusesResponseFailure(
    public val body: ValidationError,
  ) : GetTrendStatusesResponse()

  @Serializable
  public data class GetTrendStatusesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTrendStatusesResponse()
}
