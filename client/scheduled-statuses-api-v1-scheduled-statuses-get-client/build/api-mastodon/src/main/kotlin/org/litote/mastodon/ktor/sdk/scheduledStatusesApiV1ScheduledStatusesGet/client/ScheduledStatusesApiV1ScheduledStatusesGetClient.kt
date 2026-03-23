package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.ScheduledStatus

public class ScheduledStatusesApiV1ScheduledStatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View scheduled statuses
   */
  public suspend fun getScheduledStatuses(
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetScheduledStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/scheduled_statuses") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetScheduledStatusesResponseSuccess(response.body<List<ScheduledStatus>>())
        401, 404, 429, 503 -> GetScheduledStatusesResponseFailure401(response.body<Error>())
        410 -> GetScheduledStatusesResponseFailure410
        422 -> GetScheduledStatusesResponseFailure(response.body<ValidationError>())
        else -> GetScheduledStatusesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetScheduledStatusesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetScheduledStatusesResponse

  @Serializable
  public data class GetScheduledStatusesResponseSuccess(
    public val body: List<ScheduledStatus>,
  ) : GetScheduledStatusesResponse()

  @Serializable
  public data class GetScheduledStatusesResponseFailure401(
    public val body: Error,
  ) : GetScheduledStatusesResponse()

  @Serializable
  public object GetScheduledStatusesResponseFailure410 : GetScheduledStatusesResponse()

  @Serializable
  public data class GetScheduledStatusesResponseFailure(
    public val body: ValidationError,
  ) : GetScheduledStatusesResponse()

  @Serializable
  public data class GetScheduledStatusesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetScheduledStatusesResponse()
}
