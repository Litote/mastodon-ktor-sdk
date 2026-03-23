package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model.NotificationTypeEnum
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsgetE402785c.model.Notification

public class NotificationsApiV1NotificationsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get all notifications
   */
  public suspend fun getNotifications(
    accountId: String? = null,
    excludeTypes: List<NotificationTypeEnum>? = null,
    includeFiltered: Boolean? = false,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
    types: List<NotificationTypeEnum>? = null,
  ): GetNotificationsResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.append("exclude_types", excludeTypes.joinToString(","))
          }
          if (includeFiltered != null) {
            parameters.append("include_filtered", includeFiltered.toString())
          }
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
          if (types != null) {
            parameters.append("types", types.joinToString(","))
          }
        }
      }
      return when (response.status.value) {
        200 -> GetNotificationsResponseSuccess(response.body<List<Notification>>())
        401, 404, 429, 503 -> GetNotificationsResponseFailure401(response.body<Error>())
        410 -> GetNotificationsResponseFailure410
        422 -> GetNotificationsResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationsResponse

  @Serializable
  public data class GetNotificationsResponseSuccess(
    public val body: List<Notification>,
  ) : GetNotificationsResponse()

  @Serializable
  public data class GetNotificationsResponseFailure401(
    public val body: Error,
  ) : GetNotificationsResponse()

  @Serializable
  public object GetNotificationsResponseFailure410 : GetNotificationsResponse()

  @Serializable
  public data class GetNotificationsResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsResponse()

  @Serializable
  public data class GetNotificationsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsResponse()
}
