package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsIdDismissPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
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

public interface NotificationsApiV1NotificationsIdDismissPostClient {
  /**
   * Dismiss a single notification
   */
  public suspend fun postNotificationDismiss(id: String): PostNotificationDismissResponse

  @Serializable
  public sealed class PostNotificationDismissResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostNotificationDismissResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissResponse() {
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
  public data class PostNotificationDismissResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissResponse()

  @Serializable
  public data class PostNotificationDismissResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissResponse()

  @Serializable
  public data class PostNotificationDismissResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissResponse()

  @Serializable
  public data class PostNotificationDismissResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissResponse()
}

public fun NotificationsApiV1NotificationsIdDismissPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsIdDismissPostClient = DefaultNotificationsApiV1NotificationsIdDismissPostClient(configuration)

public class DefaultNotificationsApiV1NotificationsIdDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsIdDismissPostClient {
  override suspend fun postNotificationDismiss(id: String): NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/{id}/dismiss".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsIdDismissPostClient.PostNotificationDismissResponseUnknownFailure(500)
    }
  }
}
