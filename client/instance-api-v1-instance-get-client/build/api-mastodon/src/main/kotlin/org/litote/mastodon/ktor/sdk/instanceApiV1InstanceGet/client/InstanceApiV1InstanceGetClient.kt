package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceGet.client

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
import org.litote.mastodon.ktor.sdk.model.V1Instance
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV1InstanceGetClient {
  /**
   * View server information (v1)
   */
  public suspend fun getInstance(): GetInstanceResponse

  @Serializable
  public sealed class GetInstanceResponse : OperationResponse<V1Instance> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetInstanceResponseSuccess(
    override val body: V1Instance,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceResponse(),
      OperationResponseSuccess<V1Instance> {
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
  public data class GetInstanceResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetInstanceResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetInstanceResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetInstanceResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceResponse(),
      OperationResponseUnknownFailure
}

public fun InstanceApiV1InstanceGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceGetClient = DefaultInstanceApiV1InstanceGetClient(configuration)

public class DefaultInstanceApiV1InstanceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceGetClient {
  override suspend fun getInstance(): InstanceApiV1InstanceGetClient.GetInstanceResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceGetClient.GetInstanceResponseSuccess(response.body<V1Instance>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceGetClient.GetInstanceResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceGetClient.GetInstanceResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceGetClient.GetInstanceResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceGetClient.GetInstanceResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceGetClient.GetInstanceResponseUnknownFailure(500)
    }
  }
}
