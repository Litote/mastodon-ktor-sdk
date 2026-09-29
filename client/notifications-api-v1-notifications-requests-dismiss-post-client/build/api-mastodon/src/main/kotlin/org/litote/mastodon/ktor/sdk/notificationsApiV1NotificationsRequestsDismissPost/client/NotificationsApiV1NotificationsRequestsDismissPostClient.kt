package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsDismissPost.client

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

public interface NotificationsApiV1NotificationsRequestsDismissPostClient {
  /**
   * Dismiss multiple notification requests
   */
  public suspend fun createNotificationsRequestsDismiss(): CreateNotificationsRequestsDismissResponse

  @Serializable
  public sealed class CreateNotificationsRequestsDismissResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsDismissResponse() {
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
  public data class CreateNotificationsRequestsDismissResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsDismissResponse()

  @Serializable
  public data class CreateNotificationsRequestsDismissResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsDismissResponse()
}

public fun NotificationsApiV1NotificationsRequestsDismissPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsDismissPostClient = DefaultNotificationsApiV1NotificationsRequestsDismissPostClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsDismissPostClient {
  override suspend fun createNotificationsRequestsDismiss(): NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/dismiss") {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsDismissPostClient.CreateNotificationsRequestsDismissResponseUnknownFailure(500)
    }
  }
}
