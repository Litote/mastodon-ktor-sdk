package org.litote.mastodon.ktor.sdk.listsApiV1ListsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.ListRepliesPolicyEnum

public interface ListsApiV1ListsPostClient {
  /**
   * Create a list
   */
  public suspend fun createList(request: CreateListRequest): CreateListResponse

  @Serializable
  public data class CreateListRequest(
    public val exclusive: Boolean? = null,
    @SerialName("replies_policy")
    public val repliesPolicy: ListRepliesPolicyEnum? = null,
    public val title: String,
  )

  @Serializable
  public sealed class CreateListResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateListResponseSuccess(
    public val body: List,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateListResponse() {
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
  public data class CreateListResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateListResponse()

  @Serializable
  public data class CreateListResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateListResponse()

  @Serializable
  public data class CreateListResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateListResponse()
}

public fun ListsApiV1ListsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsPostClient = DefaultListsApiV1ListsPostClient(configuration)

public class DefaultListsApiV1ListsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsPostClient {
  override suspend fun createList(request: ListsApiV1ListsPostClient.CreateListRequest): ListsApiV1ListsPostClient.CreateListResponse {
    try {
      val response = configuration.client.post("api/v1/lists") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsPostClient.CreateListResponseSuccess(response.body<List>(), response.headers)
        401, 404, 422, 429, 503 -> ListsApiV1ListsPostClient.CreateListResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsPostClient.CreateListResponseFailure(response.headers)
        else -> ListsApiV1ListsPostClient.CreateListResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsPostClient.CreateListResponseUnknownFailure(500)
    }
  }
}
