package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsIdAcceptPost.client

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

public class NotificationsApiV1NotificationsRequestsIdAcceptPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Accept a single notification request
   */
  public suspend fun postNotificationsRequestsByIdAccept(id: String): PostNotificationsRequestsByIdAcceptResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/{id}/accept".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostNotificationsRequestsByIdAcceptResponseSuccess
        401, 404, 429, 503 -> PostNotificationsRequestsByIdAcceptResponseFailure401(response.body<Error>())
        410 -> PostNotificationsRequestsByIdAcceptResponseFailure410
        422 -> PostNotificationsRequestsByIdAcceptResponseFailure(response.body<ValidationError>())
        else -> PostNotificationsRequestsByIdAcceptResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostNotificationsRequestsByIdAcceptResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostNotificationsRequestsByIdAcceptResponse

  @Serializable
  public object PostNotificationsRequestsByIdAcceptResponseSuccess : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseFailure401(
    public val body: Error,
  ) : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public object PostNotificationsRequestsByIdAcceptResponseFailure410 : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseFailure(
    public val body: ValidationError,
  ) : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostNotificationsRequestsByIdAcceptResponse()
}
