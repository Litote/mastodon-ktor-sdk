package org.litote.mastodon.ktor.sdk.conversationsApiV1ConversationsIdReadPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedConversationsapiv1conversationsget8e9e61a3.model.Conversation

public interface ConversationsApiV1ConversationsIdReadPostClient {
  /**
   * Mark a conversation as read
   */
  public suspend fun postConversationRead(id: String): PostConversationReadResponse

  @Serializable
  public sealed class PostConversationReadResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostConversationReadResponseSuccess(
    public val body: Conversation,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostConversationReadResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class PostConversationReadResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostConversationReadResponse()

  @Serializable
  public data class PostConversationReadResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostConversationReadResponse()

  @Serializable
  public data class PostConversationReadResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostConversationReadResponse()

  @Serializable
  public data class PostConversationReadResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostConversationReadResponse()
}

public fun ConversationsApiV1ConversationsIdReadPostClient(configuration: ClientConfiguration = defaultClientConfiguration): ConversationsApiV1ConversationsIdReadPostClient = DefaultConversationsApiV1ConversationsIdReadPostClient(configuration)

public class DefaultConversationsApiV1ConversationsIdReadPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ConversationsApiV1ConversationsIdReadPostClient {
  override suspend fun postConversationRead(id: String): ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponse {
    try {
      val response = configuration.client.post("api/v1/conversations/{id}/read".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponseSuccess(response.body<Conversation>(), response.headers)
        401, 404, 429, 503 -> ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponseFailure401(response.body<Error>(), response.headers)
        410 -> ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponseFailure410(response.headers)
        422 -> ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ConversationsApiV1ConversationsIdReadPostClient.PostConversationReadResponseUnknownFailure(500)
    }
  }
}
