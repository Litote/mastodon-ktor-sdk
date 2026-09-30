package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGroupKeyGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv2notificationsget01d6a505.model.GroupedNotificationsResults

public interface NotificationsApiV2NotificationsGroupKeyGetClient {
  /**
   * Get a single notification group
   */
  public suspend fun getNotificationsByGroupKeyV2(groupKey: String): GetNotificationsByGroupKeyV2Response

  @Serializable
  public sealed class GetNotificationsByGroupKeyV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseSuccess(
    public val body: GroupedNotificationsResults,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsByGroupKeyV2Response() {
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
  public data class GetNotificationsByGroupKeyV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsByGroupKeyV2Response()

  @Serializable
  public data class GetNotificationsByGroupKeyV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsByGroupKeyV2Response()
}

public fun NotificationsApiV2NotificationsGroupKeyGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV2NotificationsGroupKeyGetClient = DefaultNotificationsApiV2NotificationsGroupKeyGetClient(configuration)

public class DefaultNotificationsApiV2NotificationsGroupKeyGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV2NotificationsGroupKeyGetClient {
  override suspend fun getNotificationsByGroupKeyV2(groupKey: String): NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/{group_key}".replace("/{group_key}", "/${groupKey.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2ResponseSuccess(response.body<GroupedNotificationsResults>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2ResponseFailure410(response.headers)
        422 -> NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV2NotificationsGroupKeyGetClient.GetNotificationsByGroupKeyV2ResponseUnknownFailure(500)
    }
  }
}
