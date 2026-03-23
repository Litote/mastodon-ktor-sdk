package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedPushapiv1pushsubscriptiongetEbb17244.model.WebPushSubscription

public class PushApiV1PushSubscriptionPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Subscribe to push notifications
   */
  public suspend fun createPushSubscription(request: CreatePushSubscriptionRequest): CreatePushSubscriptionResponse {
    try {
      val response = configuration.client.post("api/v1/push/subscription") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreatePushSubscriptionResponseSuccess(response.body<WebPushSubscription>())
        401, 404, 429, 503 -> CreatePushSubscriptionResponseFailure401(response.body<Error>())
        410 -> CreatePushSubscriptionResponseFailure410
        422 -> CreatePushSubscriptionResponseFailure(response.body<ValidationError>())
        else -> CreatePushSubscriptionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreatePushSubscriptionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreatePushSubscriptionRequest(
    public val `data`: Data? = null,
    public val subscription: Subscription,
  ) {
    @Serializable
    public data class Data(
      public val alerts: Alerts? = null,
      public val policy: String? = null,
    ) {
      @Serializable
      public data class Alerts(
        @SerialName("admin.report")
        public val adminReport: Boolean? = null,
        @SerialName("admin.sign_up")
        public val adminSignUp: Boolean? = null,
        public val favourite: Boolean? = null,
        public val follow: Boolean? = null,
        @SerialName("follow_request")
        public val followRequest: Boolean? = null,
        public val mention: Boolean? = null,
        public val poll: Boolean? = null,
        public val quote: Boolean? = null,
        @SerialName("quoted_update")
        public val quotedUpdate: Boolean? = null,
        public val reblog: Boolean? = null,
        public val status: Boolean? = null,
        public val update: Boolean? = null,
      )
    }

    @Serializable
    public data class Subscription(
      public val endpoint: String? = null,
      public val keys: Keys? = null,
      public val standard: Boolean? = null,
    ) {
      @Serializable
      public data class Keys(
        public val auth: String? = null,
        public val p256dh: String? = null,
      )
    }
  }

  @Serializable
  public sealed class CreatePushSubscriptionResponse

  @Serializable
  public data class CreatePushSubscriptionResponseSuccess(
    public val body: WebPushSubscription,
  ) : CreatePushSubscriptionResponse()

  @Serializable
  public data class CreatePushSubscriptionResponseFailure401(
    public val body: Error,
  ) : CreatePushSubscriptionResponse()

  @Serializable
  public object CreatePushSubscriptionResponseFailure410 : CreatePushSubscriptionResponse()

  @Serializable
  public data class CreatePushSubscriptionResponseFailure(
    public val body: ValidationError,
  ) : CreatePushSubscriptionResponse()

  @Serializable
  public data class CreatePushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreatePushSubscriptionResponse()
}
