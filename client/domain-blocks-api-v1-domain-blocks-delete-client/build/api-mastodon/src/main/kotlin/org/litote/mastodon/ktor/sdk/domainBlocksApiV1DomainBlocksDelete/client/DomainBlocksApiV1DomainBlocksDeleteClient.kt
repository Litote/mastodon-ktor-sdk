package org.litote.mastodon.ktor.sdk.domainBlocksApiV1DomainBlocksDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public interface DomainBlocksApiV1DomainBlocksDeleteClient {
  /**
   * Unblock a domain
   */
  public suspend fun deleteDomainBlocks(request: DeleteDomainBlocksRequest): DeleteDomainBlocksResponse

  @Serializable
  public data class DeleteDomainBlocksRequest(
    public val domain: String,
  )

  @Serializable
  public sealed class DeleteDomainBlocksResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteDomainBlocksResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteDomainBlocksResponse() {
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
  public data class DeleteDomainBlocksResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteDomainBlocksResponse()

  @Serializable
  public data class DeleteDomainBlocksResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteDomainBlocksResponse()

  @Serializable
  public data class DeleteDomainBlocksResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteDomainBlocksResponse()
}

public fun DomainBlocksApiV1DomainBlocksDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): DomainBlocksApiV1DomainBlocksDeleteClient = DefaultDomainBlocksApiV1DomainBlocksDeleteClient(configuration)

public class DefaultDomainBlocksApiV1DomainBlocksDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : DomainBlocksApiV1DomainBlocksDeleteClient {
  override suspend fun deleteDomainBlocks(request: DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksRequest): DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksResponse {
    try {
      val response = configuration.client.delete("api/v1/domain_blocks") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksResponseSuccess(response.headers)
        401, 404, 422, 429, 503 -> DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksResponseFailure401(response.body<Error>(), response.headers)
        410 -> DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksResponseFailure(response.headers)
        else -> DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DomainBlocksApiV1DomainBlocksDeleteClient.DeleteDomainBlocksResponseUnknownFailure(500)
    }
  }
}
