package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsAcceptPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV1NotificationsRequestsAcceptPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Accept multiple notification requests
   */
  public suspend fun createNotificationsRequestsAccept(): CreateNotificationsRequestsAcceptResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/accept") {
      }
      return when (response.status.value) {
        200 -> CreateNotificationsRequestsAcceptResponseSuccess
        401, 404, 429, 503 -> CreateNotificationsRequestsAcceptResponseFailure401(response.body<Error>())
        410 -> CreateNotificationsRequestsAcceptResponseFailure410
        422 -> CreateNotificationsRequestsAcceptResponseFailure(response.body<ValidationError>())
        else -> CreateNotificationsRequestsAcceptResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateNotificationsRequestsAcceptResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class CreateNotificationsRequestsAcceptResponse

  @Serializable
  public object CreateNotificationsRequestsAcceptResponseSuccess : CreateNotificationsRequestsAcceptResponse()

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseFailure401(
    public val body: Error,
  ) : CreateNotificationsRequestsAcceptResponse()

  @Serializable
  public object CreateNotificationsRequestsAcceptResponseFailure410 : CreateNotificationsRequestsAcceptResponse()

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseFailure(
    public val body: ValidationError,
  ) : CreateNotificationsRequestsAcceptResponse()

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateNotificationsRequestsAcceptResponse()
}
