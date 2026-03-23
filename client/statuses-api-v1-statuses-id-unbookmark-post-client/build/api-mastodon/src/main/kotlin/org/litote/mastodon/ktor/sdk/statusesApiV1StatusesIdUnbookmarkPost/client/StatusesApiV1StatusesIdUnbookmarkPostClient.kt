package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnbookmarkPost.client

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

public class StatusesApiV1StatusesIdUnbookmarkPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Undo bookmark of a status
   */
  public suspend fun postStatusUnbookmark(id: String): PostStatusUnbookmarkResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unbookmark".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusUnbookmarkResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusUnbookmarkResponseFailure401(response.body<Error>())
        410 -> PostStatusUnbookmarkResponseFailure410
        422 -> PostStatusUnbookmarkResponseFailure(response.body<ValidationError>())
        else -> PostStatusUnbookmarkResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusUnbookmarkResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusUnbookmarkResponse

  @Serializable
  public data class PostStatusUnbookmarkResponseSuccess(
    public val body: Status,
  ) : PostStatusUnbookmarkResponse()

  @Serializable
  public data class PostStatusUnbookmarkResponseFailure401(
    public val body: Error,
  ) : PostStatusUnbookmarkResponse()

  @Serializable
  public object PostStatusUnbookmarkResponseFailure410 : PostStatusUnbookmarkResponse()

  @Serializable
  public data class PostStatusUnbookmarkResponseFailure(
    public val body: ValidationError,
  ) : PostStatusUnbookmarkResponse()

  @Serializable
  public data class PostStatusUnbookmarkResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusUnbookmarkResponse()
}
