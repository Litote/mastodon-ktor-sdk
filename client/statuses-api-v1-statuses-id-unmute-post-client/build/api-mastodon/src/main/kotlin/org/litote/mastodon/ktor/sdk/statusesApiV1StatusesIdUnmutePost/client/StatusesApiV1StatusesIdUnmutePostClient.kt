package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnmutePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdUnmutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unmute a conversation
   */
  public suspend fun postStatusUnmute(id: String): PostStatusUnmuteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unmute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusUnmuteResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusUnmuteResponseFailure401(response.body<Error>())
        410 -> PostStatusUnmuteResponseFailure410
        422 -> PostStatusUnmuteResponseFailure(response.body<ValidationError>())
        else -> PostStatusUnmuteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusUnmuteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusUnmuteResponse

  @Serializable
  public data class PostStatusUnmuteResponseSuccess(
    public val body: Status,
  ) : PostStatusUnmuteResponse()

  @Serializable
  public data class PostStatusUnmuteResponseFailure401(
    public val body: Error,
  ) : PostStatusUnmuteResponse()

  @Serializable
  public object PostStatusUnmuteResponseFailure410 : PostStatusUnmuteResponse()

  @Serializable
  public data class PostStatusUnmuteResponseFailure(
    public val body: ValidationError,
  ) : PostStatusUnmuteResponse()

  @Serializable
  public data class PostStatusUnmuteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusUnmuteResponse()
}
