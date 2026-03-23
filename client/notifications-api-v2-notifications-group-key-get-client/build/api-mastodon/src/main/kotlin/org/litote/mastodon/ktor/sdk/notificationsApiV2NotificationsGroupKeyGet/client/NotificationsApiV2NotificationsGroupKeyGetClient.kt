package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGroupKeyGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv2notificationsget01d6a505.model.GroupedNotificationsResults

public class NotificationsApiV2NotificationsGroupKeyGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get a single notification group
   */
  public suspend fun getNotificationsByGroupKeyV2(groupKey: String): GetNotificationsByGroupKeyV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/{group_key}".replace("/{group_key}", "/${groupKey.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetNotificationsByGroupKeyV2ResponseSuccess(response.body<GroupedNotificationsResults>())
        401, 404, 429, 503 -> GetNotificationsByGroupKeyV2ResponseFailure401(response.body<Error>())
        410 -> GetNotificationsByGroupKeyV2ResponseFailure410
        422 -> GetNotificationsByGroupKeyV2ResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsByGroupKeyV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsByGroupKeyV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationsByGroupKeyV2Response

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseSuccess(
    public val body: GroupedNotificationsResults,
  ) : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseFailure401(
    public val body: Error,
  ) : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public object GetNotificationsByGroupKeyV2ResponseFailure410 : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsByGroupKeyV2Response()
}
