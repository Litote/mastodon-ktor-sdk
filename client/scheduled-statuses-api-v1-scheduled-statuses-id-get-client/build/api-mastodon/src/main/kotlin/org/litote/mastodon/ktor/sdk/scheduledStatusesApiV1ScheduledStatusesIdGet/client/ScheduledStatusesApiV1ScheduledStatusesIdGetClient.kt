package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.ScheduledStatus

public class ScheduledStatusesApiV1ScheduledStatusesIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a single scheduled status
   */
  public suspend fun getScheduledStatus(id: String): GetScheduledStatusResponse {
    try {
      val response = configuration.client.`get`("api/v1/scheduled_statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetScheduledStatusResponseSuccess(response.body<ScheduledStatus>())
        401, 404, 429, 503 -> GetScheduledStatusResponseFailure401(response.body<Error>())
        410 -> GetScheduledStatusResponseFailure410
        422 -> GetScheduledStatusResponseFailure(response.body<ValidationError>())
        else -> GetScheduledStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetScheduledStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetScheduledStatusResponse

  @Serializable
  public data class GetScheduledStatusResponseSuccess(
    public val body: ScheduledStatus,
  ) : GetScheduledStatusResponse()

  @Serializable
  public data class GetScheduledStatusResponseFailure401(
    public val body: Error,
  ) : GetScheduledStatusResponse()

  @Serializable
  public object GetScheduledStatusResponseFailure410 : GetScheduledStatusResponse()

  @Serializable
  public data class GetScheduledStatusResponseFailure(
    public val body: ValidationError,
  ) : GetScheduledStatusResponse()

  @Serializable
  public data class GetScheduledStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetScheduledStatusResponse()
}
