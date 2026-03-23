package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsIdDismissPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV1NotificationsIdDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Dismiss a single notification
   */
  public suspend fun postNotificationDismiss(id: String): PostNotificationDismissResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/{id}/dismiss".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostNotificationDismissResponseSuccess
        401, 404, 429, 503 -> PostNotificationDismissResponseFailure401(response.body<Error>())
        410 -> PostNotificationDismissResponseFailure410
        422 -> PostNotificationDismissResponseFailure(response.body<ValidationError>())
        else -> PostNotificationDismissResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostNotificationDismissResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostNotificationDismissResponse

  @Serializable
  public object PostNotificationDismissResponseSuccess : PostNotificationDismissResponse()

  @Serializable
  public data class PostNotificationDismissResponseFailure401(
    public val body: Error,
  ) : PostNotificationDismissResponse()

  @Serializable
  public object PostNotificationDismissResponseFailure410 : PostNotificationDismissResponse()

  @Serializable
  public data class PostNotificationDismissResponseFailure(
    public val body: ValidationError,
  ) : PostNotificationDismissResponse()

  @Serializable
  public data class PostNotificationDismissResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostNotificationDismissResponse()
}
