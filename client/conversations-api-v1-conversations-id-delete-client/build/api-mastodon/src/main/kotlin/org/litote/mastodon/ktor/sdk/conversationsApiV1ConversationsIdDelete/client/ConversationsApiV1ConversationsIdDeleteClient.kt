package org.litote.mastodon.ktor.sdk.conversationsApiV1ConversationsIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
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

public interface ConversationsApiV1ConversationsIdDeleteClient {
  /**
   * Remove a conversation
   */
  public suspend fun deleteConversation(id: String): DeleteConversationResponse

  @Serializable
  public sealed class DeleteConversationResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteConversationResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteConversationResponse() {
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
  public data class DeleteConversationResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteConversationResponse()

  @Serializable
  public data class DeleteConversationResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteConversationResponse()

  @Serializable
  public data class DeleteConversationResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteConversationResponse()

  @Serializable
  public data class DeleteConversationResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteConversationResponse()
}

public fun ConversationsApiV1ConversationsIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): ConversationsApiV1ConversationsIdDeleteClient = DefaultConversationsApiV1ConversationsIdDeleteClient(configuration)

public class DefaultConversationsApiV1ConversationsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ConversationsApiV1ConversationsIdDeleteClient {
  override suspend fun deleteConversation(id: String): ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponse {
    try {
      val response = configuration.client.delete("api/v1/conversations/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponseSuccess(response.headers)
        401, 404, 429, 503 -> ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponseFailure401(response.body<Error>(), response.headers)
        410 -> ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponseFailure410(response.headers)
        422 -> ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ConversationsApiV1ConversationsIdDeleteClient.DeleteConversationResponseUnknownFailure(500)
    }
  }
}
