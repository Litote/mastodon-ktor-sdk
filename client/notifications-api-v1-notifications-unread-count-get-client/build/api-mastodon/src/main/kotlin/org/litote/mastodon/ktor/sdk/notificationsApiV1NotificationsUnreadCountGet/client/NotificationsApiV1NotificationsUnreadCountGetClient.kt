package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsUnreadCountGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsunreadcountget188564c9.model.CountResponse

public class NotificationsApiV1NotificationsUnreadCountGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get the number of unread notifications
   */
  public suspend fun getNotificationsUnreadCount(
    accountId: String? = null,
    excludeTypes: List<String>? = null,
    limit: Long? = 100,
    types: List<String>? = null,
  ): GetNotificationsUnreadCountResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/unread_count") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.append("exclude_types", excludeTypes.joinToString(","))
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (types != null) {
            parameters.append("types", types.joinToString(","))
          }
        }
      }
      return when (response.status.value) {
        200 -> GetNotificationsUnreadCountResponseSuccess(response.body<CountResponse>())
        401, 404, 429, 503 -> GetNotificationsUnreadCountResponseFailure401(response.body<Error>())
        410 -> GetNotificationsUnreadCountResponseFailure410
        422 -> GetNotificationsUnreadCountResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsUnreadCountResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsUnreadCountResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object ExcludeTypes

  @Serializable
  public object Types

  @Serializable
  public sealed class GetNotificationsUnreadCountResponse

  @Serializable
  public data class GetNotificationsUnreadCountResponseSuccess(
    public val body: CountResponse,
  ) : GetNotificationsUnreadCountResponse()

  @Serializable
  public data class GetNotificationsUnreadCountResponseFailure401(
    public val body: Error,
  ) : GetNotificationsUnreadCountResponse()

  @Serializable
  public object GetNotificationsUnreadCountResponseFailure410 : GetNotificationsUnreadCountResponse()

  @Serializable
  public data class GetNotificationsUnreadCountResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsUnreadCountResponse()

  @Serializable
  public data class GetNotificationsUnreadCountResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsUnreadCountResponse()
}
