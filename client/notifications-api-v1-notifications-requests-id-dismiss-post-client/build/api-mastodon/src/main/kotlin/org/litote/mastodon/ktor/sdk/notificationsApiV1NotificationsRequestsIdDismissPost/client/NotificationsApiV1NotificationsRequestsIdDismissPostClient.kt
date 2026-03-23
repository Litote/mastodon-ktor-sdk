package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsIdDismissPost.client

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

public class NotificationsApiV1NotificationsRequestsIdDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Dismiss a single notification request
   */
  public suspend fun postNotificationsRequestsByIdDismiss(id: String): PostNotificationsRequestsByIdDismissResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/{id}/dismiss".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostNotificationsRequestsByIdDismissResponseSuccess
        401, 404, 429, 503 -> PostNotificationsRequestsByIdDismissResponseFailure401(response.body<Error>())
        410 -> PostNotificationsRequestsByIdDismissResponseFailure410
        422 -> PostNotificationsRequestsByIdDismissResponseFailure(response.body<ValidationError>())
        else -> PostNotificationsRequestsByIdDismissResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostNotificationsRequestsByIdDismissResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostNotificationsRequestsByIdDismissResponse

  @Serializable
  public object PostNotificationsRequestsByIdDismissResponseSuccess : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseFailure401(
    public val body: Error,
  ) : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public object PostNotificationsRequestsByIdDismissResponseFailure410 : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseFailure(
    public val body: ValidationError,
  ) : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostNotificationsRequestsByIdDismissResponse()
}
