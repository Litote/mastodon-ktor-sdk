package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsPolicyGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.NotificationPolicy
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV2NotificationsPolicyGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get the filtering policy for notifications
   */
  public suspend fun getNotificationPolicyV2(): GetNotificationPolicyV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/policy") {
      }
      return when (response.status.value) {
        200 -> GetNotificationPolicyV2ResponseSuccess(response.body<NotificationPolicy>())
        401, 404, 429, 503 -> GetNotificationPolicyV2ResponseFailure401(response.body<Error>())
        410 -> GetNotificationPolicyV2ResponseFailure410
        422 -> GetNotificationPolicyV2ResponseFailure(response.body<ValidationError>())
        else -> GetNotificationPolicyV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationPolicyV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationPolicyV2Response

  @Serializable
  public data class GetNotificationPolicyV2ResponseSuccess(
    public val body: NotificationPolicy,
  ) : GetNotificationPolicyV2Response()

  @Serializable
  public data class GetNotificationPolicyV2ResponseFailure401(
    public val body: Error,
  ) : GetNotificationPolicyV2Response()

  @Serializable
  public object GetNotificationPolicyV2ResponseFailure410 : GetNotificationPolicyV2Response()

  @Serializable
  public data class GetNotificationPolicyV2ResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationPolicyV2Response()

  @Serializable
  public data class GetNotificationPolicyV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationPolicyV2Response()
}
