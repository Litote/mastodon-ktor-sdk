package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsIdDismissPost.client

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

public interface NotificationsApiV1NotificationsRequestsIdDismissPostClient {
  /**
   * Dismiss a single notification request
   */
  public suspend fun postNotificationsRequestsByIdDismiss(id: String): PostNotificationsRequestsByIdDismissResponse

  @Serializable
  public sealed class PostNotificationsRequestsByIdDismissResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdDismissResponse() {
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
  public data class PostNotificationsRequestsByIdDismissResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdDismissResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdDismissResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdDismissResponse()
}

public fun NotificationsApiV1NotificationsRequestsIdDismissPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsIdDismissPostClient = DefaultNotificationsApiV1NotificationsRequestsIdDismissPostClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsIdDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsIdDismissPostClient {
  override suspend fun postNotificationsRequestsByIdDismiss(id: String): NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/{id}/dismiss".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsIdDismissPostClient.PostNotificationsRequestsByIdDismissResponseUnknownFailure(500)
    }
  }
}
