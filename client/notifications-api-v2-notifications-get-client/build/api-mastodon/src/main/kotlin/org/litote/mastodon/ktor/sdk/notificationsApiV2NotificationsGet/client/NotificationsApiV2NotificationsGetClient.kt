package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv2notificationsget01d6a505.model.GroupedNotificationsResults

public class NotificationsApiV2NotificationsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get all grouped notifications
   */
  public suspend fun getNotificationsV2(
    accountId: String? = null,
    excludeTypes: List<NotificationTypeEnum>? = null,
    expandAccounts: String? = null,
    groupedTypes: List<NotificationTypeEnum>? = null,
    includeFiltered: Boolean? = false,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
    types: List<NotificationTypeEnum>? = null,
  ): GetNotificationsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.append("exclude_types", excludeTypes.joinToString(","))
          }
          if (expandAccounts != null) {
            parameters.append("expand_accounts", expandAccounts)
          }
          if (groupedTypes != null) {
            parameters.append("grouped_types", groupedTypes.joinToString(","))
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
        200 -> GetNotificationsV2ResponseSuccess(response.body<GroupedNotificationsResults>())
        401, 404, 429, 503 -> GetNotificationsV2ResponseFailure401(response.body<Error>())
        410 -> GetNotificationsV2ResponseFailure410
        422 -> GetNotificationsV2ResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationsV2Response

  @Serializable
  public data class GetNotificationsV2ResponseSuccess(
    public val body: GroupedNotificationsResults,
  ) : GetNotificationsV2Response()

  @Serializable
  public data class GetNotificationsV2ResponseFailure401(
    public val body: Error,
  ) : GetNotificationsV2Response()

  @Serializable
  public object GetNotificationsV2ResponseFailure410 : GetNotificationsV2Response()

  @Serializable
  public data class GetNotificationsV2ResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsV2Response()

  @Serializable
  public data class GetNotificationsV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsV2Response()
}
