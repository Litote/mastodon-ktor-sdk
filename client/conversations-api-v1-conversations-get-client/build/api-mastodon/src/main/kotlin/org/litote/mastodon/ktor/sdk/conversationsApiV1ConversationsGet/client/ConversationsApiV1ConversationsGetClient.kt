package org.litote.mastodon.ktor.sdk.conversationsApiV1ConversationsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedConversationsapiv1conversationsget8e9e61a3.model.Conversation

public class ConversationsApiV1ConversationsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View all conversations
   */
  public suspend fun getConversations(
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetConversationsResponse {
    try {
      val response = configuration.client.`get`("api/v1/conversations") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetConversationsResponseSuccess(response.body<List<Conversation>>())
        401, 404, 429, 503 -> GetConversationsResponseFailure401(response.body<Error>())
        410 -> GetConversationsResponseFailure410
        422 -> GetConversationsResponseFailure(response.body<ValidationError>())
        else -> GetConversationsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetConversationsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetConversationsResponse

  @Serializable
  public data class GetConversationsResponseSuccess(
    public val body: List<Conversation>,
  ) : GetConversationsResponse()

  @Serializable
  public data class GetConversationsResponseFailure401(
    public val body: Error,
  ) : GetConversationsResponse()

  @Serializable
  public object GetConversationsResponseFailure410 : GetConversationsResponse()

  @Serializable
  public data class GetConversationsResponseFailure(
    public val body: ValidationError,
  ) : GetConversationsResponse()

  @Serializable
  public data class GetConversationsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetConversationsResponse()
}
