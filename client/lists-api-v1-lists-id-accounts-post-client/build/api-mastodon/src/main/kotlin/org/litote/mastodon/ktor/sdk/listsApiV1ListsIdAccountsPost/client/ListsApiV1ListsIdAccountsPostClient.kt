package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdAccountsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public interface ListsApiV1ListsIdAccountsPostClient {
  /**
   * Add accounts to a list
   */
  public suspend fun postListAccounts(request: PostListAccountsRequest, id: String): PostListAccountsResponse

  @Serializable
  public data class PostListAccountsRequest(
    @SerialName("account_ids")
    public val accountIds: List<String>,
  )

  @Serializable
  public sealed class PostListAccountsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostListAccountsResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostListAccountsResponse() {
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
  public data class PostListAccountsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostListAccountsResponse()

  @Serializable
  public data class PostListAccountsResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostListAccountsResponse()

  @Serializable
  public data class PostListAccountsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostListAccountsResponse()
}

public fun ListsApiV1ListsIdAccountsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsIdAccountsPostClient = DefaultListsApiV1ListsIdAccountsPostClient(configuration)

public class DefaultListsApiV1ListsIdAccountsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsIdAccountsPostClient {
  override suspend fun postListAccounts(request: ListsApiV1ListsIdAccountsPostClient.PostListAccountsRequest, id: String): ListsApiV1ListsIdAccountsPostClient.PostListAccountsResponse {
    try {
      val response = configuration.client.post("api/v1/lists/{id}/accounts".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsIdAccountsPostClient.PostListAccountsResponseSuccess(response.headers)
        401, 404, 422, 429, 503 -> ListsApiV1ListsIdAccountsPostClient.PostListAccountsResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsIdAccountsPostClient.PostListAccountsResponseFailure(response.headers)
        else -> ListsApiV1ListsIdAccountsPostClient.PostListAccountsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsIdAccountsPostClient.PostListAccountsResponseUnknownFailure(500)
    }
  }
}
