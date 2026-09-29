package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdDelete.client

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

public interface ListsApiV1ListsIdDeleteClient {
  /**
   * Delete a list
   */
  public suspend fun deleteList(id: String): DeleteListResponse

  @Serializable
  public sealed class DeleteListResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteListResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListResponse() {
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
  public data class DeleteListResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListResponse()

  @Serializable
  public data class DeleteListResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListResponse()

  @Serializable
  public data class DeleteListResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListResponse()

  @Serializable
  public data class DeleteListResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListResponse()
}

public fun ListsApiV1ListsIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsIdDeleteClient = DefaultListsApiV1ListsIdDeleteClient(configuration)

public class DefaultListsApiV1ListsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsIdDeleteClient {
  override suspend fun deleteList(id: String): ListsApiV1ListsIdDeleteClient.DeleteListResponse {
    try {
      val response = configuration.client.delete("api/v1/lists/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsIdDeleteClient.DeleteListResponseSuccess(response.headers)
        401, 404, 429, 503 -> ListsApiV1ListsIdDeleteClient.DeleteListResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsIdDeleteClient.DeleteListResponseFailure410(response.headers)
        422 -> ListsApiV1ListsIdDeleteClient.DeleteListResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ListsApiV1ListsIdDeleteClient.DeleteListResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsIdDeleteClient.DeleteListResponseUnknownFailure(500)
    }
  }
}
