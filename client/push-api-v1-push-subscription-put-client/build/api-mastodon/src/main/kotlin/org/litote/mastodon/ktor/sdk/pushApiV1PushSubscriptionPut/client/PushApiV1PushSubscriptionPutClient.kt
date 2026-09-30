package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
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

public interface PushApiV1PushSubscriptionPutClient {
  /**
   * Change types of notifications
   */
  public suspend fun putPushSubscription(request: PutPushSubscriptionRequest): PutPushSubscriptionResponse

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
  public sealed class PutPushSubscriptionResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PutPushSubscriptionResponseSuccess(
    public val body: WebPushSubscription,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PutPushSubscriptionResponse() {
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
  public data class PutPushSubscriptionResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PutPushSubscriptionResponse()

  @Serializable
  public data class PutPushSubscriptionResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PutPushSubscriptionResponse()

  @Serializable
  public data class PutPushSubscriptionResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PutPushSubscriptionResponse()

  @Serializable
  public data class PutPushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PutPushSubscriptionResponse()
}

public fun PushApiV1PushSubscriptionPutClient(configuration: ClientConfiguration = defaultClientConfiguration): PushApiV1PushSubscriptionPutClient = DefaultPushApiV1PushSubscriptionPutClient(configuration)

public class DefaultPushApiV1PushSubscriptionPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PushApiV1PushSubscriptionPutClient {
  override suspend fun putPushSubscription(request: PushApiV1PushSubscriptionPutClient.PutPushSubscriptionRequest): PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponse {
    try {
      val response = configuration.client.put("api/v1/push/subscription") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponseSuccess(response.body<WebPushSubscription>(), response.headers)
        401, 404, 429, 503 -> PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponseFailure401(response.body<Error>(), response.headers)
        410 -> PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponseFailure410(response.headers)
        422 -> PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponseFailure(response.body<ValidationError>(), response.headers)
        else -> PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PushApiV1PushSubscriptionPutClient.PutPushSubscriptionResponseUnknownFailure(500)
    }
  }
}
