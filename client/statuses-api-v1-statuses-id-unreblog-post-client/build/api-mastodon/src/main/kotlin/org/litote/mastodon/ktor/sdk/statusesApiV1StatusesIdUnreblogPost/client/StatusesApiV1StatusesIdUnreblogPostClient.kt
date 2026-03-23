package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdUnreblogPost.client

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

public class StatusesApiV1StatusesIdUnreblogPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Undo boost of a status
   */
  public suspend fun postStatusUnreblog(id: String): PostStatusUnreblogResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/unreblog".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusUnreblogResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusUnreblogResponseFailure401(response.body<Error>())
        410 -> PostStatusUnreblogResponseFailure410
        422 -> PostStatusUnreblogResponseFailure(response.body<ValidationError>())
        else -> PostStatusUnreblogResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusUnreblogResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusUnreblogResponse

  @Serializable
  public data class PostStatusUnreblogResponseSuccess(
    public val body: Status,
  ) : PostStatusUnreblogResponse()

  @Serializable
  public data class PostStatusUnreblogResponseFailure401(
    public val body: Error,
  ) : PostStatusUnreblogResponse()

  @Serializable
  public object PostStatusUnreblogResponseFailure410 : PostStatusUnreblogResponse()

  @Serializable
  public data class PostStatusUnreblogResponseFailure(
    public val body: ValidationError,
  ) : PostStatusUnreblogResponse()

  @Serializable
  public data class PostStatusUnreblogResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusUnreblogResponse()
}
