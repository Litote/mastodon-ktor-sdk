package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceDomainBlocksGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.DomainBlock
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV1InstanceDomainBlocksGetClient {
  /**
   * View moderated servers
   */
  public suspend fun getInstanceDomainBlocks(): GetInstanceDomainBlocksResponse

  @Serializable
  public sealed class GetInstanceDomainBlocksResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstanceDomainBlocksResponseSuccess(
    public val body: List<DomainBlock>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceDomainBlocksResponse() {
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
  public data class GetInstanceDomainBlocksResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceDomainBlocksResponse()

  @Serializable
  public data class GetInstanceDomainBlocksResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceDomainBlocksResponse()

  @Serializable
  public data class GetInstanceDomainBlocksResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceDomainBlocksResponse()

  @Serializable
  public data class GetInstanceDomainBlocksResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceDomainBlocksResponse()
}

public fun InstanceApiV1InstanceDomainBlocksGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceDomainBlocksGetClient = DefaultInstanceApiV1InstanceDomainBlocksGetClient(configuration)

public class DefaultInstanceApiV1InstanceDomainBlocksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceDomainBlocksGetClient {
  override suspend fun getInstanceDomainBlocks(): InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/domain_blocks") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponseSuccess(response.body<List<DomainBlock>>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceDomainBlocksGetClient.GetInstanceDomainBlocksResponseUnknownFailure(500)
    }
  }
}
