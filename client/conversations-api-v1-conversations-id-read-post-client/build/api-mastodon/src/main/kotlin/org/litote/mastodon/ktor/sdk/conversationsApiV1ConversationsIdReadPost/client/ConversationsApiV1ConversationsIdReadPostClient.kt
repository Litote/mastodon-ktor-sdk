package org.litote.mastodon.ktor.sdk.conversationsApiV1ConversationsIdReadPost.client

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
import org.litote.mastodon.ktor.sdk.sharedConversationsapiv1conversationsget8e9e61a3.model.Conversation

public class ConversationsApiV1ConversationsIdReadPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Mark a conversation as read
   */
  public suspend fun postConversationRead(id: String): PostConversationReadResponse {
    try {
      val response = configuration.client.post("api/v1/conversations/{id}/read".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostConversationReadResponseSuccess(response.body<Conversation>())
        401, 404, 429, 503 -> PostConversationReadResponseFailure401(response.body<Error>())
        410 -> PostConversationReadResponseFailure410
        422 -> PostConversationReadResponseFailure(response.body<ValidationError>())
        else -> PostConversationReadResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostConversationReadResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostConversationReadResponse

  @Serializable
  public data class PostConversationReadResponseSuccess(
    public val body: Conversation,
  ) : PostConversationReadResponse()

  @Serializable
  public data class PostConversationReadResponseFailure401(
    public val body: Error,
  ) : PostConversationReadResponse()

  @Serializable
  public object PostConversationReadResponseFailure410 : PostConversationReadResponse()

  @Serializable
  public data class PostConversationReadResponseFailure(
    public val body: ValidationError,
  ) : PostConversationReadResponse()

  @Serializable
  public data class PostConversationReadResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostConversationReadResponse()
}
