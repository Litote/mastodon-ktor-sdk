package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGroupKeyDismissPost.client

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

public class NotificationsApiV2NotificationsGroupKeyDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Dismiss a single notification group
   */
  public suspend fun postNotificationDismissV2(groupKey: String): PostNotificationDismissV2Response {
    try {
      val response = configuration.client.post("api/v2/notifications/{group_key}/dismiss".replace("/{group_key}", "/${groupKey.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostNotificationDismissV2ResponseSuccess
        401, 404, 429, 503 -> PostNotificationDismissV2ResponseFailure401(response.body<Error>())
        410 -> PostNotificationDismissV2ResponseFailure410
        422 -> PostNotificationDismissV2ResponseFailure(response.body<ValidationError>())
        else -> PostNotificationDismissV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostNotificationDismissV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostNotificationDismissV2Response

  @Serializable
  public object PostNotificationDismissV2ResponseSuccess : PostNotificationDismissV2Response()

  @Serializable
  public data class PostNotificationDismissV2ResponseFailure401(
    public val body: Error,
  ) : PostNotificationDismissV2Response()

  @Serializable
  public object PostNotificationDismissV2ResponseFailure410 : PostNotificationDismissV2Response()

  @Serializable
  public data class PostNotificationDismissV2ResponseFailure(
    public val body: ValidationError,
  ) : PostNotificationDismissV2Response()

  @Serializable
  public data class PostNotificationDismissV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostNotificationDismissV2Response()
}
