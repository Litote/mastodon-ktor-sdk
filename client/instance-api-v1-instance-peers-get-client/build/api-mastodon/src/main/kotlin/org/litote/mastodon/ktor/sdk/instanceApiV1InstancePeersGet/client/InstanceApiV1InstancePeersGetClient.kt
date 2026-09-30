package org.litote.mastodon.ktor.sdk.instanceApiV1InstancePeersGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV1InstancePeersGetClient {
  /**
   * List of connected domains
   */
  public suspend fun getInstancePeers(): GetInstancePeersResponse

  @Serializable
  public sealed class GetInstancePeersResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstancePeersResponseSuccess(
    public val body: List<String>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePeersResponse() {
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
  public data class GetInstancePeersResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePeersResponse()

  @Serializable
  public data class GetInstancePeersResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePeersResponse()

  @Serializable
  public data class GetInstancePeersResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePeersResponse()

  @Serializable
  public data class GetInstancePeersResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePeersResponse()
}

public fun InstanceApiV1InstancePeersGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstancePeersGetClient = DefaultInstanceApiV1InstancePeersGetClient(configuration)

public class DefaultInstanceApiV1InstancePeersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstancePeersGetClient {
  override suspend fun getInstancePeers(): InstanceApiV1InstancePeersGetClient.GetInstancePeersResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/peers") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstancePeersGetClient.GetInstancePeersResponseSuccess(response.body<List<String>>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstancePeersGetClient.GetInstancePeersResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstancePeersGetClient.GetInstancePeersResponseFailure410(response.headers)
        422 -> InstanceApiV1InstancePeersGetClient.GetInstancePeersResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstancePeersGetClient.GetInstancePeersResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstancePeersGetClient.GetInstancePeersResponseUnknownFailure(500)
    }
  }
}
