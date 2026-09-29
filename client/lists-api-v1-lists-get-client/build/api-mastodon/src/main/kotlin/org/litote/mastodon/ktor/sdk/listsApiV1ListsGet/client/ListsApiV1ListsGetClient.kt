package org.litote.mastodon.ktor.sdk.listsApiV1ListsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import kotlin.collections.List as CollectionsList
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List as ModelList

public interface ListsApiV1ListsGetClient {
  /**
   * View your lists
   */
  public suspend fun getLists(): GetListsResponse

  @Serializable
  public sealed class GetListsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetListsResponseSuccess(
    public val body: CollectionsList<ModelList>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListsResponse() {
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
  public data class GetListsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListsResponse()

  @Serializable
  public data class GetListsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListsResponse()

  @Serializable
  public data class GetListsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListsResponse()

  @Serializable
  public data class GetListsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetListsResponse()
}

public fun ListsApiV1ListsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsGetClient = DefaultListsApiV1ListsGetClient(configuration)

public class DefaultListsApiV1ListsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsGetClient {
  override suspend fun getLists(): ListsApiV1ListsGetClient.GetListsResponse {
    try {
      val response = configuration.client.`get`("api/v1/lists") {
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsGetClient.GetListsResponseSuccess(response.body<CollectionsList<ModelList>>(), response.headers)
        401, 404, 429, 503 -> ListsApiV1ListsGetClient.GetListsResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsGetClient.GetListsResponseFailure410(response.headers)
        422 -> ListsApiV1ListsGetClient.GetListsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ListsApiV1ListsGetClient.GetListsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsGetClient.GetListsResponseUnknownFailure(500)
    }
  }
}
