package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGroupKeyAccountsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface NotificationsApiV2NotificationsGroupKeyAccountsGetClient {
  /**
   * Get accounts of all notifications in a notification group
   */
  public suspend fun getNotificationAccountsV2(groupKey: String): GetNotificationAccountsV2Response

  @Serializable
  public sealed class GetNotificationAccountsV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationAccountsV2ResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationAccountsV2Response() {
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
  public data class GetNotificationAccountsV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationAccountsV2Response()

  @Serializable
  public data class GetNotificationAccountsV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationAccountsV2Response()

  @Serializable
  public data class GetNotificationAccountsV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationAccountsV2Response()

  @Serializable
  public data class GetNotificationAccountsV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationAccountsV2Response()
}

public fun NotificationsApiV2NotificationsGroupKeyAccountsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV2NotificationsGroupKeyAccountsGetClient = DefaultNotificationsApiV2NotificationsGroupKeyAccountsGetClient(configuration)

public class DefaultNotificationsApiV2NotificationsGroupKeyAccountsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV2NotificationsGroupKeyAccountsGetClient {
  override suspend fun getNotificationAccountsV2(groupKey: String): NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/{group_key}/accounts".replace("/{group_key}", "/${groupKey.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2ResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2ResponseFailure410(response.headers)
        422 -> NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV2NotificationsGroupKeyAccountsGetClient.GetNotificationAccountsV2ResponseUnknownFailure(500)
    }
  }
}
