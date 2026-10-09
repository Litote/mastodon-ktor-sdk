package org.litote.mastodon.ktor.sdk.api.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.api.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.api.model.Error
import org.litote.mastodon.ktor.sdk.api.model.Notification
import org.litote.mastodon.ktor.sdk.api.model.NotificationTypeEnum
import org.litote.mastodon.ktor.sdk.api.model.ValidationError

public interface NotificationsClient {
  /**
   * Get all notifications
   */
  public suspend fun getNotifications(
    accountId: String? = null,
    excludeTypes: List<NotificationTypeEnum>? = null,
    includeFiltered: Boolean? = false,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
    types: List<NotificationTypeEnum>? = null,
  ): GetNotificationsResponse

  @Serializable
  public sealed class GetNotificationsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationsResponseSuccess(
    public val body: List<Notification>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsResponse() {
    /**
     * Pagination links for browsing older or newer results. Format: Link: <https://mastodon.example/api/v1/endpoint?max_id=7163058>; rel="next", <https://mastodon.example/api/v1/endpoint?min_id=7275607>; rel="prev". See [RFC 8288](https://www.rfc-editor.org/rfc/rfc8288) for more information.
     */
    public val link: String?
      get() = headers["Link"]

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
  public data class GetNotificationsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsResponse()

  @Serializable
  public data class GetNotificationsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsResponse()

  @Serializable
  public data class GetNotificationsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsResponse()

  @Serializable
  public data class GetNotificationsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsResponse()
}

public fun NotificationsClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsClient = DefaultNotificationsClient(configuration)

public class DefaultNotificationsClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsClient {
  override suspend fun getNotifications(
    accountId: String?,
    excludeTypes: List<NotificationTypeEnum>?,
    includeFiltered: Boolean?,
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
    types: List<NotificationTypeEnum>?,
  ): NotificationsClient.GetNotificationsResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.appendAll("exclude_types", excludeTypes.map { it.serialName() })
          }
          if (includeFiltered != null) {
            parameters.append("include_filtered", includeFiltered.toString())
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
          if (types != null) {
            parameters.appendAll("types", types.map { it.serialName() })
          }
        }
      }
      return when (response.status.value) {
        200 -> NotificationsClient.GetNotificationsResponseSuccess(response.body<List<Notification>>(), response.headers)
        401, 404, 429, 503 -> NotificationsClient.GetNotificationsResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsClient.GetNotificationsResponseFailure410(response.headers)
        422 -> NotificationsClient.GetNotificationsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsClient.GetNotificationsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsClient.GetNotificationsResponseUnknownFailure(500)
    }
  }
}
