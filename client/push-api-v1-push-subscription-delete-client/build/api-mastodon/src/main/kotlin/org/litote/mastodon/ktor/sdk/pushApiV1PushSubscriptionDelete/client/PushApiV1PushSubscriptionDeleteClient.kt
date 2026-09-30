package org.litote.mastodon.ktor.sdk.pushApiV1PushSubscriptionDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
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

public interface PushApiV1PushSubscriptionDeleteClient {
  /**
   * Remove current subscription
   */
  public suspend fun deletePushSubscription(): DeletePushSubscriptionResponse

  @Serializable
  public sealed class DeletePushSubscriptionResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeletePushSubscriptionResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeletePushSubscriptionResponse() {
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
  public data class DeletePushSubscriptionResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeletePushSubscriptionResponse()

  @Serializable
  public data class DeletePushSubscriptionResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeletePushSubscriptionResponse()

  @Serializable
  public data class DeletePushSubscriptionResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeletePushSubscriptionResponse()

  @Serializable
  public data class DeletePushSubscriptionResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeletePushSubscriptionResponse()
}

public fun PushApiV1PushSubscriptionDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): PushApiV1PushSubscriptionDeleteClient = DefaultPushApiV1PushSubscriptionDeleteClient(configuration)

public class DefaultPushApiV1PushSubscriptionDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PushApiV1PushSubscriptionDeleteClient {
  override suspend fun deletePushSubscription(): PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponse {
    try {
      val response = configuration.client.delete("api/v1/push/subscription") {
      }
      return when (response.status.value) {
        200 -> PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponseSuccess(response.headers)
        401, 404, 429, 503 -> PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponseFailure401(response.body<Error>(), response.headers)
        410 -> PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponseFailure410(response.headers)
        422 -> PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponseFailure(response.body<ValidationError>(), response.headers)
        else -> PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PushApiV1PushSubscriptionDeleteClient.DeletePushSubscriptionResponseUnknownFailure(500)
    }
  }
}
