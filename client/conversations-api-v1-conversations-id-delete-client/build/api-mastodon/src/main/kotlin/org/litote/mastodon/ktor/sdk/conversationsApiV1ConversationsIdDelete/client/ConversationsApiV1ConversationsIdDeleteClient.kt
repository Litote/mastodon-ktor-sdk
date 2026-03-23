package org.litote.mastodon.ktor.sdk.conversationsApiV1ConversationsIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class ConversationsApiV1ConversationsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove a conversation
   */
  public suspend fun deleteConversation(id: String): DeleteConversationResponse {
    try {
      val response = configuration.client.delete("api/v1/conversations/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteConversationResponseSuccess
        401, 404, 429, 503 -> DeleteConversationResponseFailure401(response.body<Error>())
        410 -> DeleteConversationResponseFailure410
        422 -> DeleteConversationResponseFailure(response.body<ValidationError>())
        else -> DeleteConversationResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteConversationResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteConversationResponse

  @Serializable
  public object DeleteConversationResponseSuccess : DeleteConversationResponse()

  @Serializable
  public data class DeleteConversationResponseFailure401(
    public val body: Error,
  ) : DeleteConversationResponse()

  @Serializable
  public object DeleteConversationResponseFailure410 : DeleteConversationResponse()

  @Serializable
  public data class DeleteConversationResponseFailure(
    public val body: ValidationError,
  ) : DeleteConversationResponse()

  @Serializable
  public data class DeleteConversationResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteConversationResponse()
}
