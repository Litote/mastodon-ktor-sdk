package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsPolicyGet.client

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
import org.litote.mastodon.ktor.sdk.model.NotificationPolicy
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface NotificationsApiV2NotificationsPolicyGetClient {
  /**
   * Get the filtering policy for notifications
   */
  public suspend fun getNotificationPolicyV2(): GetNotificationPolicyV2Response

  @Serializable
  public sealed class GetNotificationPolicyV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationPolicyV2ResponseSuccess(
    public val body: NotificationPolicy,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationPolicyV2Response() {
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
  public data class GetNotificationPolicyV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationPolicyV2Response()

  @Serializable
  public data class GetNotificationPolicyV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationPolicyV2Response()

  @Serializable
  public data class GetNotificationPolicyV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationPolicyV2Response()

  @Serializable
  public data class GetNotificationPolicyV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationPolicyV2Response()
}

public fun NotificationsApiV2NotificationsPolicyGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV2NotificationsPolicyGetClient = DefaultNotificationsApiV2NotificationsPolicyGetClient(configuration)

public class DefaultNotificationsApiV2NotificationsPolicyGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV2NotificationsPolicyGetClient {
  override suspend fun getNotificationPolicyV2(): NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/policy") {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2ResponseSuccess(response.body<NotificationPolicy>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2ResponseFailure410(response.headers)
        422 -> NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV2NotificationsPolicyGetClient.GetNotificationPolicyV2ResponseUnknownFailure(500)
    }
  }
}
