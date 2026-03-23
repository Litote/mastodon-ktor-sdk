package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsUnreadCountGet.client

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

public class NotificationsApiV2NotificationsUnreadCountGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get the number of unread notifications
   */
  public suspend fun getNotificationsUnreadCountV2(
    accountId: String? = null,
    excludeTypes: List<String>? = null,
    groupedTypes: List<String>? = null,
    limit: Long? = 100,
    types: List<String>? = null,
  ): GetNotificationsUnreadCountV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/unread_count") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.append("exclude_types", excludeTypes.joinToString(","))
          }
          if (groupedTypes != null) {
            parameters.append("grouped_types", groupedTypes.joinToString(","))
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
        200 -> GetNotificationsUnreadCountV2ResponseSuccess(response.body<CountResponse>())
        401, 404, 429, 503 -> GetNotificationsUnreadCountV2ResponseFailure401(response.body<Error>())
        410 -> GetNotificationsUnreadCountV2ResponseFailure410
        422 -> GetNotificationsUnreadCountV2ResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsUnreadCountV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsUnreadCountV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object ExcludeTypes

  @Serializable
  public object GroupedTypes

  @Serializable
  public object Types

  @Serializable
  public sealed class GetNotificationsUnreadCountV2Response

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseSuccess(
    public val body: CountResponse,
  ) : GetNotificationsUnreadCountV2Response()

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseFailure401(
    public val body: Error,
  ) : GetNotificationsUnreadCountV2Response()

  @Serializable
  public object GetNotificationsUnreadCountV2ResponseFailure410 : GetNotificationsUnreadCountV2Response()

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsUnreadCountV2Response()

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsUnreadCountV2Response()
}
