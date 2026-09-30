package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsGet.client

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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsrequestsget7fa167cd.model.NotificationRequest

public interface NotificationsApiV1NotificationsRequestsGetClient {
  /**
   * Get all notification requests
   */
  public suspend fun getNotificationRequests(
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetNotificationRequestsResponse

  @Serializable
  public sealed class GetNotificationRequestsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationRequestsResponseSuccess(
    public val body: List<NotificationRequest>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationRequestsResponse() {
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
  public data class GetNotificationRequestsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationRequestsResponse()

  @Serializable
  public data class GetNotificationRequestsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationRequestsResponse()

  @Serializable
  public data class GetNotificationRequestsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationRequestsResponse()

  @Serializable
  public data class GetNotificationRequestsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationRequestsResponse()
}

public fun NotificationsApiV1NotificationsRequestsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsGetClient = DefaultNotificationsApiV1NotificationsRequestsGetClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsGetClient {
  override suspend fun getNotificationRequests(
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/requests") {
        url {
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
        }
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponseSuccess(response.body<List<NotificationRequest>>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsGetClient.GetNotificationRequestsResponseUnknownFailure(500)
    }
  }
}
