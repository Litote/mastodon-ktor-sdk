package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdPinPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdPinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Pin status to profile
   */
  public suspend fun postStatusPin(id: String): PostStatusPinResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/pin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusPinResponseSuccess(response.body<Status>())
        401, 404, 422, 429, 503 -> PostStatusPinResponseFailure401(response.body<Error>())
        410 -> PostStatusPinResponseFailure
        else -> PostStatusPinResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusPinResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusPinResponse

  @Serializable
  public data class PostStatusPinResponseSuccess(
    public val body: Status,
  ) : PostStatusPinResponse()

  @Serializable
  public data class PostStatusPinResponseFailure401(
    public val body: Error,
  ) : PostStatusPinResponse()

  @Serializable
  public object PostStatusPinResponseFailure : PostStatusPinResponse()

  @Serializable
  public data class PostStatusPinResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusPinResponse()
}
