package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
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

public class PushApiV1PushSubscriptionPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Change types of notifications
   */
  public suspend fun putPushSubscription(request: PutPushSubscriptionRequest): PutPushSubscriptionResponse {
    try {
      val response = configuration.client.put("api/v1/push/subscription") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PutPushSubscriptionResponseSuccess(response.body<WebPushSubscription>())
        401, 404, 429, 503 -> PutPushSubscriptionResponseFailure401(response.body<Error>())
        410 -> PutPushSubscriptionResponseFailure410
        422 -> PutPushSubscriptionResponseFailure(response.body<ValidationError>())
        else -> PutPushSubscriptionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PutPushSubscriptionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PutPushSubscriptionRequest(
    public val `data`: Data? = null,
    public val policy: String? = null,
  ) {
    @Serializable
    public data class Data(
      public val alerts: Alerts? = null,
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
        public val reblog: Boolean? = null,
        public val status: Boolean? = null,
        public val update: Boolean? = null,
      )
    }
  }

  @Serializable
  public sealed class PutPushSubscriptionResponse

  @Serializable
  public data class PutPushSubscriptionResponseSuccess(
    public val body: WebPushSubscription,
  ) : PutPushSubscriptionResponse()

  @Serializable
  public data class PutPushSubscriptionResponseFailure401(
    public val body: Error,
  ) : PutPushSubscriptionResponse()

  @Serializable
  public object PutPushSubscriptionResponseFailure410 : PutPushSubscriptionResponse()

  @Serializable
  public data class PutPushSubscriptionResponseFailure(
    public val body: ValidationError,
  ) : PutPushSubscriptionResponse()

  @Serializable
  public data class PutPushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
  ) : PutPushSubscriptionResponse()
}
