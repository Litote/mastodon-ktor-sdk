package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsClearPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV1NotificationsClearPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Dismiss all notifications
   */
  public suspend fun createNotificationClear(): CreateNotificationClearResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/clear") {
      }
      return when (response.status.value) {
        200 -> CreateNotificationClearResponseSuccess
        401, 404, 429, 503 -> CreateNotificationClearResponseFailure401(response.body<Error>())
        410 -> CreateNotificationClearResponseFailure410
        422 -> CreateNotificationClearResponseFailure(response.body<ValidationError>())
        else -> CreateNotificationClearResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateNotificationClearResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class CreateNotificationClearResponse

  @Serializable
  public object CreateNotificationClearResponseSuccess : CreateNotificationClearResponse()

  @Serializable
  public data class CreateNotificationClearResponseFailure401(
    public val body: Error,
  ) : CreateNotificationClearResponse()

  @Serializable
  public object CreateNotificationClearResponseFailure410 : CreateNotificationClearResponse()

  @Serializable
  public data class CreateNotificationClearResponseFailure(
    public val body: ValidationError,
  ) : CreateNotificationClearResponse()

  @Serializable
  public data class CreateNotificationClearResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateNotificationClearResponse()
}
