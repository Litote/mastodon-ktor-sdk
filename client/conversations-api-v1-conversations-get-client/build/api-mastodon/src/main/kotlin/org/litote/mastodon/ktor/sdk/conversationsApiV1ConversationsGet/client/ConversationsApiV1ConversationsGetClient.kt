package org.litote.mastodon.ktor.sdk.conversationsApiV1ConversationsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedConversationsapiv1conversationsget8e9e61a3.model.Conversation

public interface ConversationsApiV1ConversationsGetClient {
  /**
   * View all conversations
   */
  public suspend fun getConversations(
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetConversationsResponse

  @Serializable
  public sealed class GetConversationsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetConversationsResponseSuccess(
    public val body: List<Conversation>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetConversationsResponse() {
    /**
     * Pagination links for browsing older or newer results. Format: Link: <https://mastodon.example/api/v1/endpoint?max_id=7163058>; rel="next", <https://mastodon.example/api/v1/endpoint?min_id=7275607>; rel="prev". See [RFC 8288](https://www.rfc-editor.org/rfc/rfc8288) for more information.
     */
    public val link: String?
      get() = headers["Link"]

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
  public data class GetConversationsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetConversationsResponse()

  @Serializable
  public data class GetConversationsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetConversationsResponse()

  @Serializable
  public data class GetConversationsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetConversationsResponse()

  @Serializable
  public data class GetConversationsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetConversationsResponse()
}

public fun ConversationsApiV1ConversationsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): ConversationsApiV1ConversationsGetClient = DefaultConversationsApiV1ConversationsGetClient(configuration)

public class DefaultConversationsApiV1ConversationsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ConversationsApiV1ConversationsGetClient {
  override suspend fun getConversations(
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): ConversationsApiV1ConversationsGetClient.GetConversationsResponse {
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
        200 -> ConversationsApiV1ConversationsGetClient.GetConversationsResponseSuccess(response.body<List<Conversation>>(), response.headers)
        401, 404, 429, 503 -> ConversationsApiV1ConversationsGetClient.GetConversationsResponseFailure401(response.body<Error>(), response.headers)
        410 -> ConversationsApiV1ConversationsGetClient.GetConversationsResponseFailure410(response.headers)
        422 -> ConversationsApiV1ConversationsGetClient.GetConversationsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ConversationsApiV1ConversationsGetClient.GetConversationsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ConversationsApiV1ConversationsGetClient.GetConversationsResponseUnknownFailure(500)
    }
  }
}
