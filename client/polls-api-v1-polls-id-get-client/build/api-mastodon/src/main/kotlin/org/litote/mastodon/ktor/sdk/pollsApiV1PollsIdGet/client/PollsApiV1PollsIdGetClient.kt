package org.litote.mastodon.ktor.sdk.pollsApiV1PollsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget27089f74.model.Poll

public class PollsApiV1PollsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a poll
   */
  public suspend fun getPoll(id: String): GetPollResponse {
    try {
      val response = configuration.client.`get`("api/v1/polls/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetPollResponseSuccess(response.body<Poll>())
        401, 404, 429, 503 -> GetPollResponseFailure401(response.body<Error>())
        410 -> GetPollResponseFailure410
        422 -> GetPollResponseFailure(response.body<ValidationError>())
        else -> GetPollResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetPollResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetPollResponse

  @Serializable
  public data class GetPollResponseSuccess(
    public val body: Poll,
  ) : GetPollResponse()

  @Serializable
  public data class GetPollResponseFailure401(
    public val body: Error,
  ) : GetPollResponse()

  @Serializable
  public object GetPollResponseFailure410 : GetPollResponse()

  @Serializable
  public data class GetPollResponseFailure(
    public val body: ValidationError,
  ) : GetPollResponse()

  @Serializable
  public data class GetPollResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetPollResponse()
}
