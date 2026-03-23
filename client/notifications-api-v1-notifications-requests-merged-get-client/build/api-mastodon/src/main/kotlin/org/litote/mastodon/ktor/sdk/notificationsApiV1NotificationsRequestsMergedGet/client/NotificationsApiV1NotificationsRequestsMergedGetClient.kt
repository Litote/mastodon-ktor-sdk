package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsMergedGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.MergedResponse
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class NotificationsApiV1NotificationsRequestsMergedGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Check if accepted notification requests have been merged
   */
  public suspend fun getNotificationsRequestsMerged(): GetNotificationsRequestsMergedResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/requests/merged") {
      }
      return when (response.status.value) {
        200 -> GetNotificationsRequestsMergedResponseSuccess(response.body<MergedResponse>())
        401, 404, 429, 503 -> GetNotificationsRequestsMergedResponseFailure401(response.body<Error>())
        410 -> GetNotificationsRequestsMergedResponseFailure410
        422 -> GetNotificationsRequestsMergedResponseFailure(response.body<ValidationError>())
        else -> GetNotificationsRequestsMergedResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetNotificationsRequestsMergedResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetNotificationsRequestsMergedResponse

  @Serializable
  public data class GetNotificationsRequestsMergedResponseSuccess(
    public val body: MergedResponse,
  ) : GetNotificationsRequestsMergedResponse()

  @Serializable
  public data class GetNotificationsRequestsMergedResponseFailure401(
    public val body: Error,
  ) : GetNotificationsRequestsMergedResponse()

  @Serializable
  public object GetNotificationsRequestsMergedResponseFailure410 : GetNotificationsRequestsMergedResponse()

  @Serializable
  public data class GetNotificationsRequestsMergedResponseFailure(
    public val body: ValidationError,
  ) : GetNotificationsRequestsMergedResponse()

  @Serializable
  public data class GetNotificationsRequestsMergedResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetNotificationsRequestsMergedResponse()
}
