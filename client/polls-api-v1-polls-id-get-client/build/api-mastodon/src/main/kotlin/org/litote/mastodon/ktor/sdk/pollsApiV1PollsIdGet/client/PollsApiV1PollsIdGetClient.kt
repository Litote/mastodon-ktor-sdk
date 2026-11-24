package org.litote.mastodon.ktor.sdk.pollsApiV1PollsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget27089f74.model.Poll

public interface PollsApiV1PollsIdGetClient {
  /**
   * View a poll
   */
  public suspend fun getPoll(id: String): GetPollResponse

  @Serializable
  public sealed class GetPollResponse : OperationResponse<Poll> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetPollResponseSuccess(
    override val body: Poll,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPollResponse(),
      OperationResponseSuccess<Poll> {
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
  public data class GetPollResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPollResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetPollResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPollResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetPollResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPollResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetPollResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPollResponse(),
      OperationResponseUnknownFailure
}

public fun PollsApiV1PollsIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): PollsApiV1PollsIdGetClient = DefaultPollsApiV1PollsIdGetClient(configuration)

public class DefaultPollsApiV1PollsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PollsApiV1PollsIdGetClient {
  override suspend fun getPoll(id: String): PollsApiV1PollsIdGetClient.GetPollResponse {
    try {
      val response = configuration.client.`get`("api/v1/polls/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PollsApiV1PollsIdGetClient.GetPollResponseSuccess(response.body<Poll>(), response.headers)
        401, 404, 429, 503 -> PollsApiV1PollsIdGetClient.GetPollResponseFailure401(response.body<Error>(), response.headers)
        410 -> PollsApiV1PollsIdGetClient.GetPollResponseFailure410(response.headers)
        422 -> PollsApiV1PollsIdGetClient.GetPollResponseFailure(response.body<ValidationError>(), response.headers)
        else -> PollsApiV1PollsIdGetClient.GetPollResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PollsApiV1PollsIdGetClient.GetPollResponseUnknownFailure(500)
    }
  }
}
