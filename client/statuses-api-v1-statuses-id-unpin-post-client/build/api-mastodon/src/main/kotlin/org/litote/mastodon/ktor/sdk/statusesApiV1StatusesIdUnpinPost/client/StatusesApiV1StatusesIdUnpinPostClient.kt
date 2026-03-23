package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnpinPost.client

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

public class StatusesApiV1StatusesIdUnpinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unpin status from profile
   */
  public suspend fun postStatusUnpin(id: String): PostStatusUnpinResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unpin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusUnpinResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusUnpinResponseFailure401(response.body<Error>())
        410 -> PostStatusUnpinResponseFailure410
        422 -> PostStatusUnpinResponseFailure(response.body<ValidationError>())
        else -> PostStatusUnpinResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusUnpinResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusUnpinResponse

  @Serializable
  public data class PostStatusUnpinResponseSuccess(
    public val body: Status,
  ) : PostStatusUnpinResponse()

  @Serializable
  public data class PostStatusUnpinResponseFailure401(
    public val body: Error,
  ) : PostStatusUnpinResponse()

  @Serializable
  public object PostStatusUnpinResponseFailure410 : PostStatusUnpinResponse()

  @Serializable
  public data class PostStatusUnpinResponseFailure(
    public val body: ValidationError,
  ) : PostStatusUnpinResponse()

  @Serializable
  public data class PostStatusUnpinResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusUnpinResponse()
}
