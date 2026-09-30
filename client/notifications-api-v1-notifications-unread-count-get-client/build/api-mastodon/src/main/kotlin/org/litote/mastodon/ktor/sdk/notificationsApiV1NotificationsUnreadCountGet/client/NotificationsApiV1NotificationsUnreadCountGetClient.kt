package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsUnreadCountGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsunreadcountget188564c9.model.CountResponse

public interface NotificationsApiV1NotificationsUnreadCountGetClient {
  /**
   * Get the number of unread notifications
   */
  public suspend fun getNotificationsUnreadCount(
    accountId: String? = null,
    excludeTypes: List<String>? = null,
    limit: Long? = 100,
    types: List<String>? = null,
  ): GetNotificationsUnreadCountResponse

  @Serializable
  public sealed class GetNotificationsUnreadCountResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationsUnreadCountResponseSuccess(
    public val body: CountResponse,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountResponse() {
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
  public data class GetNotificationsUnreadCountResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountResponse()

  @Serializable
  public data class GetNotificationsUnreadCountResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountResponse()

  @Serializable
  public data class GetNotificationsUnreadCountResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountResponse()

  @Serializable
  public data class GetNotificationsUnreadCountResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountResponse()
}

public fun NotificationsApiV1NotificationsUnreadCountGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsUnreadCountGetClient = DefaultNotificationsApiV1NotificationsUnreadCountGetClient(configuration)

public class DefaultNotificationsApiV1NotificationsUnreadCountGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsUnreadCountGetClient {
  override suspend fun getNotificationsUnreadCount(
    accountId: String?,
    excludeTypes: List<String>?,
    limit: Long?,
    types: List<String>?,
  ): NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/unread_count") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.appendAll("exclude_types", excludeTypes)
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (types != null) {
            parameters.appendAll("types", types)
          }
        }
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponseSuccess(response.body<CountResponse>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsUnreadCountGetClient.GetNotificationsUnreadCountResponseUnknownFailure(500)
    }
  }
}
