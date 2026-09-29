package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
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

public interface ListsApiV1ListsIdPutClient {
  /**
   * Update a list
   */
  public suspend fun updateList(request: UpdateListRequest, id: String): UpdateListResponse

  @Serializable
  public data class UpdateListRequest(
    public val exclusive: Boolean? = null,
    @SerialName("replies_policy")
    public val repliesPolicy: ListRepliesPolicyEnum? = null,
    public val title: String,
  )

  @Serializable
  public sealed class UpdateListResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateListResponseSuccess(
    public val body: List,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateListResponse() {
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
  public data class UpdateListResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateListResponse()

  @Serializable
  public data class UpdateListResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateListResponse()

  @Serializable
  public data class UpdateListResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateListResponse()
}

public fun ListsApiV1ListsIdPutClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsIdPutClient = DefaultListsApiV1ListsIdPutClient(configuration)

public class DefaultListsApiV1ListsIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsIdPutClient {
  override suspend fun updateList(request: ListsApiV1ListsIdPutClient.UpdateListRequest, id: String): ListsApiV1ListsIdPutClient.UpdateListResponse {
    try {
      val response = configuration.client.put("api/v1/lists/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsIdPutClient.UpdateListResponseSuccess(response.body<List>(), response.headers)
        401, 404, 422, 429, 503 -> ListsApiV1ListsIdPutClient.UpdateListResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsIdPutClient.UpdateListResponseFailure(response.headers)
        else -> ListsApiV1ListsIdPutClient.UpdateListResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsIdPutClient.UpdateListResponseUnknownFailure(500)
    }
  }
}
