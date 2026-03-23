package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsgetE402785c.model.Notification

public class NotificationsApiV1NotificationsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get a single notification
   */
  public suspend fun getNotification(id: String): GetNotificationResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetNotificationResponseSuccess(response.body<Notification>())
        401, 404, 429, 503 -> GetNotificationResponseFailure401(response.body<Error>())
        410 -> GetNotificationResponseFailure410
        422 -> GetNotificationResponseFailure(response.body<ValidationError>())
        else -> GetNotificationResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationResponse

  @Serializable
  public data class GetNotificationResponseSuccess(
    public val body: Notification,
  ) : GetNotificationResponse()

  @Serializable
  public data class GetNotificationResponseFailure401(
    public val body: Error,
  ) : GetNotificationResponse()

  @Serializable
  public object GetNotificationResponseFailure410 : GetNotificationResponse()

  @Serializable
  public data class GetNotificationResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationResponse()

  @Serializable
  public data class GetNotificationResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationResponse()
}
