package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedPushapiv1pushsubscriptiongetEbb17244.model.WebPushSubscription

public class PushApiV1PushSubscriptionGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get current subscription
   */
  public suspend fun getPushSubscription(): GetPushSubscriptionResponse {
    try {
      val response = configuration.client.`get`("api/v1/push/subscription") {
      }
      return when (response.status.value) {
        200 -> GetPushSubscriptionResponseSuccess(response.body<WebPushSubscription>())
        401, 404, 429, 503 -> GetPushSubscriptionResponseFailure401(response.body<Error>())
        410 -> GetPushSubscriptionResponseFailure410
        422 -> GetPushSubscriptionResponseFailure(response.body<ValidationError>())
        else -> GetPushSubscriptionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetPushSubscriptionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetPushSubscriptionResponse

  @Serializable
  public data class GetPushSubscriptionResponseSuccess(
    public val body: WebPushSubscription,
  ) : GetPushSubscriptionResponse()

  @Serializable
  public data class GetPushSubscriptionResponseFailure401(
    public val body: Error,
  ) : GetPushSubscriptionResponse()

  @Serializable
  public object GetPushSubscriptionResponseFailure410 : GetPushSubscriptionResponse()

  @Serializable
  public data class GetPushSubscriptionResponseFailure(
    public val body: ValidationError,
  ) : GetPushSubscriptionResponse()

  @Serializable
  public data class GetPushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetPushSubscriptionResponse()
}
