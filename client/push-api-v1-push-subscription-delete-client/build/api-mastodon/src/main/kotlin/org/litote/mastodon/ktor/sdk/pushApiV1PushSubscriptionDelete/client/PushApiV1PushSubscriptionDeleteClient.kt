package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class PushApiV1PushSubscriptionDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove current subscription
   */
  public suspend fun deletePushSubscription(): DeletePushSubscriptionResponse {
    try {
      val response = configuration.client.delete("api/v1/push/subscription") {
      }
      return when (response.status.value) {
        200 -> DeletePushSubscriptionResponseSuccess
        401, 404, 429, 503 -> DeletePushSubscriptionResponseFailure401(response.body<Error>())
        410 -> DeletePushSubscriptionResponseFailure410
        422 -> DeletePushSubscriptionResponseFailure(response.body<ValidationError>())
        else -> DeletePushSubscriptionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeletePushSubscriptionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeletePushSubscriptionResponse

  @Serializable
  public object DeletePushSubscriptionResponseSuccess : DeletePushSubscriptionResponse()

  @Serializable
  public data class DeletePushSubscriptionResponseFailure401(
    public val body: Error,
  ) : DeletePushSubscriptionResponse()

  @Serializable
  public object DeletePushSubscriptionResponseFailure410 : DeletePushSubscriptionResponse()

  @Serializable
  public data class DeletePushSubscriptionResponseFailure(
    public val body: ValidationError,
  ) : DeletePushSubscriptionResponse()

  @Serializable
  public data class DeletePushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeletePushSubscriptionResponse()
}
