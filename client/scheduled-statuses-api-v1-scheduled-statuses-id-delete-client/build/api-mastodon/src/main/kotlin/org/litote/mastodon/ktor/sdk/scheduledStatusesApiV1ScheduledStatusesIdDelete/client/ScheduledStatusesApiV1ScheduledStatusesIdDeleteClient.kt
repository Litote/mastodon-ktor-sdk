package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesIdDelete.client

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

public class ScheduledStatusesApiV1ScheduledStatusesIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Cancel a scheduled status
   */
  public suspend fun deleteScheduledStatus(id: String): DeleteScheduledStatusResponse {
    try {
      val response = configuration.client.delete("api/v1/scheduled_statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteScheduledStatusResponseSuccess
        401, 404, 429, 503 -> DeleteScheduledStatusResponseFailure401(response.body<Error>())
        410 -> DeleteScheduledStatusResponseFailure410
        422 -> DeleteScheduledStatusResponseFailure(response.body<ValidationError>())
        else -> DeleteScheduledStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteScheduledStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteScheduledStatusResponse

  @Serializable
  public object DeleteScheduledStatusResponseSuccess : DeleteScheduledStatusResponse()

  @Serializable
  public data class DeleteScheduledStatusResponseFailure401(
    public val body: Error,
  ) : DeleteScheduledStatusResponse()

  @Serializable
  public object DeleteScheduledStatusResponseFailure410 : DeleteScheduledStatusResponse()

  @Serializable
  public data class DeleteScheduledStatusResponseFailure(
    public val body: ValidationError,
  ) : DeleteScheduledStatusResponse()

  @Serializable
  public data class DeleteScheduledStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteScheduledStatusResponse()
}
