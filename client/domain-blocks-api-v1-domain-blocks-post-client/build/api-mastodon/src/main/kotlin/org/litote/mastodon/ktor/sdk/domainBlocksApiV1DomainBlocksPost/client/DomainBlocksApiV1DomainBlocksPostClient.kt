package org.litote.mastodon.ktor.sdk.domainBlocksApiV1DomainBlocksPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
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

public interface DomainBlocksApiV1DomainBlocksPostClient {
  /**
   * Block a domain
   */
  public suspend fun createDomainBlock(request: CreateDomainBlockRequest): CreateDomainBlockResponse

  @Serializable
  public data class CreateDomainBlockRequest(
    public val domain: String,
  )

  @Serializable
  public sealed class CreateDomainBlockResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateDomainBlockResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateDomainBlockResponse() {
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
  public data class CreateDomainBlockResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateDomainBlockResponse()

  @Serializable
  public data class CreateDomainBlockResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateDomainBlockResponse()

  @Serializable
  public data class CreateDomainBlockResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateDomainBlockResponse()
}

public fun DomainBlocksApiV1DomainBlocksPostClient(configuration: ClientConfiguration = defaultClientConfiguration): DomainBlocksApiV1DomainBlocksPostClient = DefaultDomainBlocksApiV1DomainBlocksPostClient(configuration)

public class DefaultDomainBlocksApiV1DomainBlocksPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : DomainBlocksApiV1DomainBlocksPostClient {
  override suspend fun createDomainBlock(request: DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockRequest): DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockResponse {
    try {
      val response = configuration.client.post("api/v1/domain_blocks") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockResponseSuccess(response.headers)
        401, 404, 422, 429, 503 -> DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockResponseFailure401(response.body<Error>(), response.headers)
        410 -> DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockResponseFailure(response.headers)
        else -> DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DomainBlocksApiV1DomainBlocksPostClient.CreateDomainBlockResponseUnknownFailure(500)
    }
  }
}
