package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdHistoryGet.client

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
import org.litote.mastodon.ktor.sdk.model.StatusEdit
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface StatusesApiV1StatusesIdHistoryGetClient {
  /**
   * View edit history of a status
   */
  public suspend fun getStatusHistory(id: String): GetStatusHistoryResponse

  @Serializable
  public sealed class GetStatusHistoryResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusHistoryResponseSuccess(
    public val body: List<StatusEdit>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusHistoryResponse() {
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
  public data class GetStatusHistoryResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusHistoryResponse()

  @Serializable
  public data class GetStatusHistoryResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusHistoryResponse()

  @Serializable
  public data class GetStatusHistoryResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusHistoryResponse()

  @Serializable
  public data class GetStatusHistoryResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusHistoryResponse()
}

public fun StatusesApiV1StatusesIdHistoryGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdHistoryGetClient = DefaultStatusesApiV1StatusesIdHistoryGetClient(configuration)

public class DefaultStatusesApiV1StatusesIdHistoryGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdHistoryGetClient {
  override suspend fun getStatusHistory(id: String): StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/history".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponseSuccess(response.body<List<StatusEdit>>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdHistoryGetClient.GetStatusHistoryResponseUnknownFailure(500)
    }
  }
}
