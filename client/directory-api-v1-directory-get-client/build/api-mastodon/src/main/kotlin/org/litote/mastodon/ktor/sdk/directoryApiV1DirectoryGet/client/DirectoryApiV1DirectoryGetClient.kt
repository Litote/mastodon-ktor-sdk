package org.litote.mastodon.ktor.sdk.directoryApiV1DirectoryGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Boolean
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface DirectoryApiV1DirectoryGetClient {
  /**
   * View profile directory
   */
  public suspend fun getDirectory(
    limit: Long? = 40,
    local: Boolean? = null,
    offset: Long? = null,
    order: String? = null,
  ): GetDirectoryResponse

  @Serializable
  public sealed class GetDirectoryResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetDirectoryResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDirectoryResponse() {
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
  public data class GetDirectoryResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDirectoryResponse()

  @Serializable
  public data class GetDirectoryResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDirectoryResponse()

  @Serializable
  public data class GetDirectoryResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDirectoryResponse()

  @Serializable
  public data class GetDirectoryResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDirectoryResponse()
}

public fun DirectoryApiV1DirectoryGetClient(configuration: ClientConfiguration = defaultClientConfiguration): DirectoryApiV1DirectoryGetClient = DefaultDirectoryApiV1DirectoryGetClient(configuration)

public class DefaultDirectoryApiV1DirectoryGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : DirectoryApiV1DirectoryGetClient {
  override suspend fun getDirectory(
    limit: Long?,
    local: Boolean?,
    offset: Long?,
    order: String?,
  ): DirectoryApiV1DirectoryGetClient.GetDirectoryResponse {
    try {
      val response = configuration.client.`get`("api/v1/directory") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (local != null) {
            parameters.append("local", local.toString())
          }
          if (offset != null) {
            parameters.append("offset", offset.toString())
          }
          if (order != null) {
            parameters.append("order", order)
          }
        }
      }
      return when (response.status.value) {
        200 -> DirectoryApiV1DirectoryGetClient.GetDirectoryResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> DirectoryApiV1DirectoryGetClient.GetDirectoryResponseFailure401(response.body<Error>(), response.headers)
        410 -> DirectoryApiV1DirectoryGetClient.GetDirectoryResponseFailure410(response.headers)
        422 -> DirectoryApiV1DirectoryGetClient.GetDirectoryResponseFailure(response.body<ValidationError>(), response.headers)
        else -> DirectoryApiV1DirectoryGetClient.GetDirectoryResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DirectoryApiV1DirectoryGetClient.GetDirectoryResponseUnknownFailure(500)
    }
  }
}
