package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdBlockPost.client

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

public class AccountsApiV1AccountsIdBlockPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Block account
   */
  public suspend fun postAccountBlock(id: String): PostAccountBlockResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/block".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountBlockResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountBlockResponseFailure401(response.body<Error>())
        410 -> PostAccountBlockResponseFailure
        else -> PostAccountBlockResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountBlockResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountBlockResponse

  @Serializable
  public data class PostAccountBlockResponseSuccess(
    public val body: Relationship,
  ) : PostAccountBlockResponse()

  @Serializable
  public data class PostAccountBlockResponseFailure401(
    public val body: Error,
  ) : PostAccountBlockResponse()

  @Serializable
  public object PostAccountBlockResponseFailure : PostAccountBlockResponse()

  @Serializable
  public data class PostAccountBlockResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountBlockResponse()
}
