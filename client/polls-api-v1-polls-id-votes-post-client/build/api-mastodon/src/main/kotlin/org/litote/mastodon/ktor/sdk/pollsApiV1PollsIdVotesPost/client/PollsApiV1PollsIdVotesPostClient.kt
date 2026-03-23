package org.litote.mastodon.ktor.sdk.pollsApiV1PollsIdVotesPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget27089f74.model.Poll

public class PollsApiV1PollsIdVotesPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Vote on a poll
   */
  public suspend fun postPollVotes(request: PostPollVotesRequest, id: String): PostPollVotesResponse {
    try {
      val response = configuration.client.post("api/v1/polls/{id}/votes".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostPollVotesResponseSuccess(response.body<Poll>())
        401, 404, 422, 429, 503 -> PostPollVotesResponseFailure401(response.body<Error>())
        410 -> PostPollVotesResponseFailure
        else -> PostPollVotesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostPollVotesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostPollVotesRequest(
    public val choices: List<Long>,
  )

  @Serializable
  public sealed class PostPollVotesResponse

  @Serializable
  public data class PostPollVotesResponseSuccess(
    public val body: Poll,
  ) : PostPollVotesResponse()

  @Serializable
  public data class PostPollVotesResponseFailure401(
    public val body: Error,
  ) : PostPollVotesResponse()

  @Serializable
  public object PostPollVotesResponseFailure : PostPollVotesResponse()

  @Serializable
  public data class PostPollVotesResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostPollVotesResponse()
}
