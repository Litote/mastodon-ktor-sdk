package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List

public interface ListsApiV1ListsIdGetClient {
  /**
   * Show a single list
   */
  public suspend fun getList(id: String): GetListResponse

  @Serializable
  public sealed class GetListResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetListResponseSuccess(
    public val body: List,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListResponse() {
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
  public data class GetListResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListResponse()

  @Serializable
  public data class GetListResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListResponse()

  @Serializable
  public data class GetListResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListResponse()

  @Serializable
  public data class GetListResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListResponse()
}

public fun ListsApiV1ListsIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsIdGetClient = DefaultListsApiV1ListsIdGetClient(configuration)

public class DefaultListsApiV1ListsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsIdGetClient {
  override suspend fun getList(id: String): ListsApiV1ListsIdGetClient.GetListResponse {
    try {
      val response = configuration.client.`get`("api/v1/lists/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsIdGetClient.GetListResponseSuccess(response.body<List>(), response.headers)
        401, 404, 429, 503 -> ListsApiV1ListsIdGetClient.GetListResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsIdGetClient.GetListResponseFailure410(response.headers)
        422 -> ListsApiV1ListsIdGetClient.GetListResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ListsApiV1ListsIdGetClient.GetListResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsIdGetClient.GetListResponseUnknownFailure(500)
    }
  }
}
