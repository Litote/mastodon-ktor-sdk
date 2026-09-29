package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdAccountsDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface ListsApiV1ListsIdAccountsDeleteClient {
  /**
   * Remove accounts from list
   */
  public suspend fun deleteListAccounts(request: DeleteListAccountsRequest, id: String): DeleteListAccountsResponse

  @Serializable
  public data class DeleteListAccountsRequest(
    @SerialName("account_ids")
    public val accountIds: List<String>,
  )

  @Serializable
  public sealed class DeleteListAccountsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteListAccountsResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListAccountsResponse() {
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
  public data class DeleteListAccountsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListAccountsResponse()

  @Serializable
  public data class DeleteListAccountsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListAccountsResponse()

  @Serializable
  public data class DeleteListAccountsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListAccountsResponse()

  @Serializable
  public data class DeleteListAccountsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteListAccountsResponse()
}

public fun ListsApiV1ListsIdAccountsDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): ListsApiV1ListsIdAccountsDeleteClient = DefaultListsApiV1ListsIdAccountsDeleteClient(configuration)

public class DefaultListsApiV1ListsIdAccountsDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ListsApiV1ListsIdAccountsDeleteClient {
  override suspend fun deleteListAccounts(request: ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsRequest, id: String): ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponse {
    try {
      val response = configuration.client.delete("api/v1/lists/{id}/accounts".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponseSuccess(response.headers)
        401, 404, 429, 503 -> ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponseFailure401(response.body<Error>(), response.headers)
        410 -> ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponseFailure410(response.headers)
        422 -> ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ListsApiV1ListsIdAccountsDeleteClient.DeleteListAccountsResponseUnknownFailure(500)
    }
  }
}
