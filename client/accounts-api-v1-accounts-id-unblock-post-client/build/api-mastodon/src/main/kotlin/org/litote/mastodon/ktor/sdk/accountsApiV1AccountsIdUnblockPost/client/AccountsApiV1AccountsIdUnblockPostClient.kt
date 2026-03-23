package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnblockPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsIdUnblockPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unblock account
   */
  public suspend fun postAccountUnblock(id: String): PostAccountUnblockResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unblock".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountUnblockResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountUnblockResponseFailure401(response.body<Error>())
        410 -> PostAccountUnblockResponseFailure
        else -> PostAccountUnblockResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountUnblockResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountUnblockResponse

  @Serializable
  public data class PostAccountUnblockResponseSuccess(
    public val body: Relationship,
  ) : PostAccountUnblockResponse()

  @Serializable
  public data class PostAccountUnblockResponseFailure401(
    public val body: Error,
  ) : PostAccountUnblockResponse()

  @Serializable
  public object PostAccountUnblockResponseFailure : PostAccountUnblockResponse()

  @Serializable
  public data class PostAccountUnblockResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountUnblockResponse()
}
