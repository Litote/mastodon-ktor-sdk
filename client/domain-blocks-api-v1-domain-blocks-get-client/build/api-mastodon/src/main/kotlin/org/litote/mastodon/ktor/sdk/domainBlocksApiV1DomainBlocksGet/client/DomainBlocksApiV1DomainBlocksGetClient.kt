package org.litote.mastodon.ktor.sdk.domainBlocksApiV1DomainBlocksGet.client

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

public interface DomainBlocksApiV1DomainBlocksGetClient {
  /**
   * Get domain blocks
   */
  public suspend fun getDomainBlocks(
    limit: Long? = 100,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetDomainBlocksResponse

  @Serializable
  public sealed class GetDomainBlocksResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetDomainBlocksResponseSuccess(
    public val body: List<String>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDomainBlocksResponse() {
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
  public data class GetDomainBlocksResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDomainBlocksResponse()

  @Serializable
  public data class GetDomainBlocksResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDomainBlocksResponse()

  @Serializable
  public data class GetDomainBlocksResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDomainBlocksResponse()

  @Serializable
  public data class GetDomainBlocksResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetDomainBlocksResponse()
}

public fun DomainBlocksApiV1DomainBlocksGetClient(configuration: ClientConfiguration = defaultClientConfiguration): DomainBlocksApiV1DomainBlocksGetClient = DefaultDomainBlocksApiV1DomainBlocksGetClient(configuration)

public class DefaultDomainBlocksApiV1DomainBlocksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : DomainBlocksApiV1DomainBlocksGetClient {
  override suspend fun getDomainBlocks(
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
  ): DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponse {
    try {
      val response = configuration.client.`get`("api/v1/domain_blocks") {
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
        200 -> DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponseSuccess(response.body<List<String>>(), response.headers)
        401, 404, 429, 503 -> DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponseFailure401(response.body<Error>(), response.headers)
        410 -> DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponseFailure410(response.headers)
        422 -> DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponseFailure(response.body<ValidationError>(), response.headers)
        else -> DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DomainBlocksApiV1DomainBlocksGetClient.GetDomainBlocksResponseUnknownFailure(500)
    }
  }
}
