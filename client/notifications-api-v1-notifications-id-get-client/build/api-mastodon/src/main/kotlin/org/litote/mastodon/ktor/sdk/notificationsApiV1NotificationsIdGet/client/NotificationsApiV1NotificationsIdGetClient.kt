package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsgetE402785c.model.Notification

public interface NotificationsApiV1NotificationsIdGetClient {
  /**
   * Get a single notification
   */
  public suspend fun getNotification(id: String): GetNotificationResponse

  @Serializable
  public sealed class GetNotificationResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationResponseSuccess(
    public val body: Notification,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationResponse() {
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
  public data class GetNotificationResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationResponse()

  @Serializable
  public data class GetNotificationResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationResponse()

  @Serializable
  public data class GetNotificationResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationResponse()

  @Serializable
  public data class GetNotificationResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationResponse()
}

public fun NotificationsApiV1NotificationsIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsIdGetClient = DefaultNotificationsApiV1NotificationsIdGetClient(configuration)

public class DefaultNotificationsApiV1NotificationsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsIdGetClient {
  override suspend fun getNotification(id: String): NotificationsApiV1NotificationsIdGetClient.GetNotificationResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsIdGetClient.GetNotificationResponseSuccess(response.body<Notification>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsIdGetClient.GetNotificationResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsIdGetClient.GetNotificationResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsIdGetClient.GetNotificationResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsIdGetClient.GetNotificationResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsIdGetClient.GetNotificationResponseUnknownFailure(500)
    }
  }
}
