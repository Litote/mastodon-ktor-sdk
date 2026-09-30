package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedPushapiv1pushsubscriptiongetEbb17244.model.WebPushSubscription

public interface PushApiV1PushSubscriptionGetClient {
  /**
   * Get current subscription
   */
  public suspend fun getPushSubscription(): GetPushSubscriptionResponse

  @Serializable
  public sealed class GetPushSubscriptionResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetPushSubscriptionResponseSuccess(
    public val body: WebPushSubscription,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPushSubscriptionResponse() {
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
  public data class GetPushSubscriptionResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPushSubscriptionResponse()

  @Serializable
  public data class GetPushSubscriptionResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPushSubscriptionResponse()

  @Serializable
  public data class GetPushSubscriptionResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPushSubscriptionResponse()

  @Serializable
  public data class GetPushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPushSubscriptionResponse()
}

public fun PushApiV1PushSubscriptionGetClient(configuration: ClientConfiguration = defaultClientConfiguration): PushApiV1PushSubscriptionGetClient = DefaultPushApiV1PushSubscriptionGetClient(configuration)

public class DefaultPushApiV1PushSubscriptionGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PushApiV1PushSubscriptionGetClient {
  override suspend fun getPushSubscription(): PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponse {
    try {
      val response = configuration.client.`get`("api/v1/push/subscription") {
      }
      return when (response.status.value) {
        200 -> PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponseSuccess(response.body<WebPushSubscription>(), response.headers)
        401, 404, 429, 503 -> PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponseFailure401(response.body<Error>(), response.headers)
        410 -> PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponseFailure410(response.headers)
        422 -> PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponseFailure(response.body<ValidationError>(), response.headers)
        else -> PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PushApiV1PushSubscriptionGetClient.GetPushSubscriptionResponseUnknownFailure(500)
    }
  }
}
