package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdMutePost.client

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

public class StatusesApiV1StatusesIdMutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Mute a conversation
   */
  public suspend fun postStatusMute(id: String): PostStatusMuteResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/mute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusMuteResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusMuteResponseFailure401(response.body<Error>())
        410 -> PostStatusMuteResponseFailure410
        422 -> PostStatusMuteResponseFailure(response.body<ValidationError>())
        else -> PostStatusMuteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusMuteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusMuteResponse

  @Serializable
  public data class PostStatusMuteResponseSuccess(
    public val body: Status,
  ) : PostStatusMuteResponse()

  @Serializable
  public data class PostStatusMuteResponseFailure401(
    public val body: Error,
  ) : PostStatusMuteResponse()

  @Serializable
  public object PostStatusMuteResponseFailure410 : PostStatusMuteResponse()

  @Serializable
  public data class PostStatusMuteResponseFailure(
    public val body: ValidationError,
  ) : PostStatusMuteResponse()

  @Serializable
  public data class PostStatusMuteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusMuteResponse()
}
