package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsClearPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
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

public interface NotificationsApiV1NotificationsClearPostClient {
  /**
   * Dismiss all notifications
   */
  public suspend fun createNotificationClear(): CreateNotificationClearResponse

  @Serializable
  public sealed class CreateNotificationClearResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateNotificationClearResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationClearResponse() {
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
  public data class CreateNotificationClearResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationClearResponse()

  @Serializable
  public data class CreateNotificationClearResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationClearResponse()

  @Serializable
  public data class CreateNotificationClearResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationClearResponse()

  @Serializable
  public data class CreateNotificationClearResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationClearResponse()
}

public fun NotificationsApiV1NotificationsClearPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsClearPostClient = DefaultNotificationsApiV1NotificationsClearPostClient(configuration)

public class DefaultNotificationsApiV1NotificationsClearPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsClearPostClient {
  override suspend fun createNotificationClear(): NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/clear") {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsClearPostClient.CreateNotificationClearResponseUnknownFailure(500)
    }
  }
}
