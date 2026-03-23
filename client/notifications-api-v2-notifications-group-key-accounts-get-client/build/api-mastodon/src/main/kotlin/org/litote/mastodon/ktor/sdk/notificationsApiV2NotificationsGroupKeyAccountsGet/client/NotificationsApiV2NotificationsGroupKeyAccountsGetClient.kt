package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGroupKeyAccountsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV2NotificationsGroupKeyAccountsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get accounts of all notifications in a notification group
   */
  public suspend fun getNotificationAccountsV2(groupKey: String): GetNotificationAccountsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/{group_key}/accounts".replace("/{group_key}", "/${groupKey.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetNotificationAccountsV2ResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetNotificationAccountsV2ResponseFailure401(response.body<Error>())
        410 -> GetNotificationAccountsV2ResponseFailure410
        422 -> GetNotificationAccountsV2ResponseFailure(response.body<ValidationError>())
        else -> GetNotificationAccountsV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationAccountsV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationAccountsV2Response

  @Serializable
  public data class GetNotificationAccountsV2ResponseSuccess(
    public val body: List<Account>,
  ) : GetNotificationAccountsV2Response()

  @Serializable
  public data class GetNotificationAccountsV2ResponseFailure401(
    public val body: Error,
  ) : GetNotificationAccountsV2Response()

  @Serializable
  public object GetNotificationAccountsV2ResponseFailure410 : GetNotificationAccountsV2Response()

  @Serializable
  public data class GetNotificationAccountsV2ResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationAccountsV2Response()

  @Serializable
  public data class GetNotificationAccountsV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationAccountsV2Response()
}
