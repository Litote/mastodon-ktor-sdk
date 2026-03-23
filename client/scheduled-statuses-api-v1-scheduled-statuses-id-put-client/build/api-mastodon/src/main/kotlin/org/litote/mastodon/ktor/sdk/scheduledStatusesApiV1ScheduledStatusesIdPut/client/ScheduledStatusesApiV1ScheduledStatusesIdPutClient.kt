package org.litote.mastodon.ktor.sdk.scheduledStatusesApiV1ScheduledStatusesIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.ScheduledStatus

public class ScheduledStatusesApiV1ScheduledStatusesIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Update a scheduled status's publishing date
   */
  public suspend fun updateScheduledStatus(request: UpdateScheduledStatusRequest, id: String): UpdateScheduledStatusResponse {
    try {
      val response = configuration.client.put("api/v1/scheduled_statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateScheduledStatusResponseSuccess(response.body<ScheduledStatus>())
        401, 404, 422, 429, 503 -> UpdateScheduledStatusResponseFailure401(response.body<Error>())
        410 -> UpdateScheduledStatusResponseFailure
        else -> UpdateScheduledStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateScheduledStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateScheduledStatusRequest(
    @SerialName("scheduled_at")
    public val scheduledAt: String? = null,
  )

  @Serializable
  public sealed class UpdateScheduledStatusResponse

  @Serializable
  public data class UpdateScheduledStatusResponseSuccess(
    public val body: ScheduledStatus,
  ) : UpdateScheduledStatusResponse()

  @Serializable
  public data class UpdateScheduledStatusResponseFailure401(
    public val body: Error,
  ) : UpdateScheduledStatusResponse()

  @Serializable
  public object UpdateScheduledStatusResponseFailure : UpdateScheduledStatusResponse()

  @Serializable
  public data class UpdateScheduledStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateScheduledStatusResponse()
}
