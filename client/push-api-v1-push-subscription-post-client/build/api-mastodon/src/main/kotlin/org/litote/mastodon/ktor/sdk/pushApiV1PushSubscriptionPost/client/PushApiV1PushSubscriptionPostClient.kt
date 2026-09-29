package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedPushapiv1pushsubscriptiongetEbb17244.model.WebPushSubscription

public interface PushApiV1PushSubscriptionPostClient {
  /**
   * Subscribe to push notifications
   */
  public suspend fun createPushSubscription(request: CreatePushSubscriptionRequest): CreatePushSubscriptionResponse

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
  public sealed class CreatePushSubscriptionResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreatePushSubscriptionResponseSuccess(
    public val body: WebPushSubscription,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreatePushSubscriptionResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class CreatePushSubscriptionResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreatePushSubscriptionResponse()

  @Serializable
  public data class CreatePushSubscriptionResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreatePushSubscriptionResponse()

  @Serializable
  public data class CreatePushSubscriptionResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreatePushSubscriptionResponse()

  @Serializable
  public data class CreatePushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreatePushSubscriptionResponse()
}

public fun PushApiV1PushSubscriptionPostClient(configuration: ClientConfiguration = defaultClientConfiguration): PushApiV1PushSubscriptionPostClient = DefaultPushApiV1PushSubscriptionPostClient(configuration)

public class DefaultPushApiV1PushSubscriptionPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PushApiV1PushSubscriptionPostClient {
  override suspend fun createPushSubscription(request: PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionRequest): PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponse {
    try {
      val response = configuration.client.post("api/v1/push/subscription") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponseSuccess(response.body<WebPushSubscription>(), response.headers)
        401, 404, 429, 503 -> PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponseFailure401(response.body<Error>(), response.headers)
        410 -> PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponseFailure410(response.headers)
        422 -> PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponseFailure(response.body<ValidationError>(), response.headers)
        else -> PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PushApiV1PushSubscriptionPostClient.CreatePushSubscriptionResponseUnknownFailure(500)
    }
  }
}
