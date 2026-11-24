package org.litote.mastodon.ktor.sdk.instanceApiV2InstanceGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.client.OperationResponse
import org.litote.mastodon.ktor.sdk.client.OperationResponseFailure
import org.litote.mastodon.ktor.sdk.client.OperationResponseSuccess
import org.litote.mastodon.ktor.sdk.client.OperationResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.model.Instance
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV2InstanceGetClient {
  /**
   * View server information
   */
  public suspend fun getInstanceV2(): GetInstanceV2Response

  @Serializable
  public sealed class GetInstanceV2Response : OperationResponse<Instance> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetInstanceV2ResponseSuccess(
    override val body: Instance,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceV2Response(),
      OperationResponseSuccess<Instance> {
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
  public data class GetInstanceV2ResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceV2Response(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetInstanceV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceV2Response(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetInstanceV2ResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceV2Response(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetInstanceV2ResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceV2Response(),
      OperationResponseUnknownFailure
}

public fun InstanceApiV2InstanceGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV2InstanceGetClient = DefaultInstanceApiV2InstanceGetClient(configuration)

public class DefaultInstanceApiV2InstanceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV2InstanceGetClient {
  override suspend fun getInstanceV2(): InstanceApiV2InstanceGetClient.GetInstanceV2Response {
    try {
      val response = configuration.client.`get`("api/v2/instance") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV2InstanceGetClient.GetInstanceV2ResponseSuccess(response.body<Instance>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV2InstanceGetClient.GetInstanceV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV2InstanceGetClient.GetInstanceV2ResponseFailure410(response.headers)
        422 -> InstanceApiV2InstanceGetClient.GetInstanceV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV2InstanceGetClient.GetInstanceV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV2InstanceGetClient.GetInstanceV2ResponseUnknownFailure(500)
    }
  }
}
