package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsrequestsget7fa167cd.model.NotificationRequest

public class NotificationsApiV1NotificationsRequestsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get all notification requests
   */
  public suspend fun getNotificationRequests(
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetNotificationRequestsResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/requests") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetNotificationRequestsResponseSuccess(response.body<List<NotificationRequest>>())
        401, 404, 429, 503 -> GetNotificationRequestsResponseFailure401(response.body<Error>())
        410 -> GetNotificationRequestsResponseFailure410
        422 -> GetNotificationRequestsResponseFailure(response.body<ValidationError>())
        else -> GetNotificationRequestsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationRequestsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationRequestsResponse

  @Serializable
  public data class GetNotificationRequestsResponseSuccess(
    public val body: List<NotificationRequest>,
  ) : GetNotificationRequestsResponse()

  @Serializable
  public data class GetNotificationRequestsResponseFailure401(
    public val body: Error,
  ) : GetNotificationRequestsResponse()

  @Serializable
  public object GetNotificationRequestsResponseFailure410 : GetNotificationRequestsResponse()

  @Serializable
  public data class GetNotificationRequestsResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationRequestsResponse()

  @Serializable
  public data class GetNotificationRequestsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationRequestsResponse()
}
