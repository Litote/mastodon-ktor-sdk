package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsrequestsget7fa167cd.model.NotificationRequest

public class NotificationsApiV1NotificationsRequestsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get a single notification request
   */
  public suspend fun getNotificationsRequestsById(id: String): GetNotificationsRequestsByIdResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/requests/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetNotificationsRequestsByIdResponseSuccess(response.body<NotificationRequest>())
        401, 404, 429, 503 -> GetNotificationsRequestsByIdResponseFailure401(response.body<Error>())
        410 -> GetNotificationsRequestsByIdResponseFailure410
        422 -> GetNotificationsRequestsByIdResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsRequestsByIdResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsRequestsByIdResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationsRequestsByIdResponse

  @Serializable
  public data class GetNotificationsRequestsByIdResponseSuccess(
    public val body: NotificationRequest,
  ) : GetNotificationsRequestsByIdResponse()

  @Serializable
  public data class GetNotificationsRequestsByIdResponseFailure401(
    public val body: Error,
  ) : GetNotificationsRequestsByIdResponse()

  @Serializable
  public object GetNotificationsRequestsByIdResponseFailure410 : GetNotificationsRequestsByIdResponse()

  @Serializable
  public data class GetNotificationsRequestsByIdResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsRequestsByIdResponse()

  @Serializable
  public data class GetNotificationsRequestsByIdResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsRequestsByIdResponse()
}
