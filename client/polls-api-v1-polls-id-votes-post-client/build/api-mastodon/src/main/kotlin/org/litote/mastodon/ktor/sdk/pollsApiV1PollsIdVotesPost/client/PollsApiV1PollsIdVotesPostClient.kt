package org.litote.mastodon.ktor.sdk.pollsApiV1PollsIdVotesPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget27089f74.model.Poll

public interface PollsApiV1PollsIdVotesPostClient {
  /**
   * Vote on a poll
   */
  public suspend fun postPollVotes(request: PostPollVotesRequest, id: String): PostPollVotesResponse

  @Serializable
  public data class PostPollVotesRequest(
    public val choices: List<Long>,
  )

  @Serializable
  public sealed class PostPollVotesResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostPollVotesResponseSuccess(
    public val body: Poll,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostPollVotesResponse() {
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
  public data class PostPollVotesResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostPollVotesResponse()

  @Serializable
  public data class PostPollVotesResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostPollVotesResponse()

  @Serializable
  public data class PostPollVotesResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostPollVotesResponse()
}

public fun PollsApiV1PollsIdVotesPostClient(configuration: ClientConfiguration = defaultClientConfiguration): PollsApiV1PollsIdVotesPostClient = DefaultPollsApiV1PollsIdVotesPostClient(configuration)

public class DefaultPollsApiV1PollsIdVotesPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PollsApiV1PollsIdVotesPostClient {
  override suspend fun postPollVotes(request: PollsApiV1PollsIdVotesPostClient.PostPollVotesRequest, id: String): PollsApiV1PollsIdVotesPostClient.PostPollVotesResponse {
    try {
      val response = configuration.client.post("api/v1/polls/{id}/votes".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PollsApiV1PollsIdVotesPostClient.PostPollVotesResponseSuccess(response.body<Poll>(), response.headers)
        401, 404, 422, 429, 503 -> PollsApiV1PollsIdVotesPostClient.PostPollVotesResponseFailure401(response.body<Error>(), response.headers)
        410 -> PollsApiV1PollsIdVotesPostClient.PostPollVotesResponseFailure(response.headers)
        else -> PollsApiV1PollsIdVotesPostClient.PostPollVotesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PollsApiV1PollsIdVotesPostClient.PostPollVotesResponseUnknownFailure(500)
    }
  }
}
