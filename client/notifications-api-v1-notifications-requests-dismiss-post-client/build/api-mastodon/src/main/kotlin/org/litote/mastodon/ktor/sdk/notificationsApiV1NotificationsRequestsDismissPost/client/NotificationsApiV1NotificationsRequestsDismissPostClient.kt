package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsDismissPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV1NotificationsRequestsDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Dismiss multiple notification requests
   */
  public suspend fun createNotificationsRequestsDismiss(): CreateNotificationsRequestsDismissResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/dismiss") {
      }
      return when (response.status.value) {
        200 -> CreateNotificationsRequestsDismissResponseSuccess
        401, 404, 429, 503 -> CreateNotificationsRequestsDismissResponseFailure401(response.body<Error>())
        410 -> CreateNotificationsRequestsDismissResponseFailure410
        422 -> CreateNotificationsRequestsDismissResponseFailure(response.body<ValidationError>())
        else -> CreateNotificationsRequestsDismissResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateNotificationsRequestsDismissResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class CreateNotificationsRequestsDismissResponse

  @Serializable
  public object CreateNotificationsRequestsDismissResponseSuccess : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseFailure401(
    public val body: Error,
  ) : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public object CreateNotificationsRequestsDismissResponseFailure410 : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseFailure(
    public val body: ValidationError,
  ) : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateNotificationsRequestsDismissResponse()
}
