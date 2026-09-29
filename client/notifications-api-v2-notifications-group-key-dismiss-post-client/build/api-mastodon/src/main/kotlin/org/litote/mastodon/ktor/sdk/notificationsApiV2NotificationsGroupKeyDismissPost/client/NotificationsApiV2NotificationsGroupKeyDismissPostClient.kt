package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGroupKeyDismissPost.client

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

public interface NotificationsApiV2NotificationsGroupKeyDismissPostClient {
  /**
   * Dismiss a single notification group
   */
  public suspend fun postNotificationDismissV2(groupKey: String): PostNotificationDismissV2Response

  @Serializable
  public sealed class PostNotificationDismissV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostNotificationDismissV2ResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissV2Response() {
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
  public data class PostNotificationDismissV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissV2Response()

  @Serializable
  public data class PostNotificationDismissV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissV2Response()

  @Serializable
  public data class PostNotificationDismissV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissV2Response()

  @Serializable
  public data class PostNotificationDismissV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationDismissV2Response()
}

public fun NotificationsApiV2NotificationsGroupKeyDismissPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV2NotificationsGroupKeyDismissPostClient = DefaultNotificationsApiV2NotificationsGroupKeyDismissPostClient(configuration)

public class DefaultNotificationsApiV2NotificationsGroupKeyDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV2NotificationsGroupKeyDismissPostClient {
  override suspend fun postNotificationDismissV2(groupKey: String): NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2Response {
    try {
      val response = configuration.client.post("api/v2/notifications/{group_key}/dismiss".replace("/{group_key}", "/${groupKey.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2ResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2ResponseFailure410(response.headers)
        422 -> NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV2NotificationsGroupKeyDismissPostClient.PostNotificationDismissV2ResponseUnknownFailure(500)
    }
  }
}
