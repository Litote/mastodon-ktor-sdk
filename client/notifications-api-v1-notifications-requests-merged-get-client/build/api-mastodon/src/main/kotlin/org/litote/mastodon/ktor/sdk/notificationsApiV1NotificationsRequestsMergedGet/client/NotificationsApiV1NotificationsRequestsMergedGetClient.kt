package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsMergedGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.client.OperationResponse
import org.litote.mastodon.ktor.sdk.client.OperationResponseFailure
import org.litote.mastodon.ktor.sdk.client.OperationResponseSuccess
import org.litote.mastodon.ktor.sdk.client.OperationResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.model.MergedResponse
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface NotificationsApiV1NotificationsRequestsMergedGetClient {
  /**
   * Check if accepted notification requests have been merged
   */
  public suspend fun getNotificationsRequestsMerged(): GetNotificationsRequestsMergedResponse

  @Serializable
  public sealed class GetNotificationsRequestsMergedResponse : OperationResponse<MergedResponse> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetNotificationsRequestsMergedResponseSuccess(
    override val body: MergedResponse,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsMergedResponse(),
      OperationResponseSuccess<MergedResponse> {
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
  public data class GetNotificationsRequestsMergedResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsMergedResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetNotificationsRequestsMergedResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsMergedResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetNotificationsRequestsMergedResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsMergedResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetNotificationsRequestsMergedResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsMergedResponse(),
      OperationResponseUnknownFailure
}

public fun NotificationsApiV1NotificationsRequestsMergedGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsMergedGetClient = DefaultNotificationsApiV1NotificationsRequestsMergedGetClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsMergedGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsMergedGetClient {
  override suspend fun getNotificationsRequestsMerged(): NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/requests/merged") {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponseSuccess(response.body<MergedResponse>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsMergedGetClient.GetNotificationsRequestsMergedResponseUnknownFailure(500)
    }
  }
}
