package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceExtendedDescriptionGet.client

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
import org.litote.mastodon.ktor.sdk.model.ExtendedDescription
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV1InstanceExtendedDescriptionGetClient {
  /**
   * View extended description
   */
  public suspend fun getInstanceExtendedDescription(): GetInstanceExtendedDescriptionResponse

  @Serializable
  public sealed class GetInstanceExtendedDescriptionResponse : OperationResponse<ExtendedDescription> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseSuccess(
    override val body: ExtendedDescription,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceExtendedDescriptionResponse(),
      OperationResponseSuccess<ExtendedDescription> {
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
  public data class GetInstanceExtendedDescriptionResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceExtendedDescriptionResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceExtendedDescriptionResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceExtendedDescriptionResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetInstanceExtendedDescriptionResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceExtendedDescriptionResponse(),
      OperationResponseUnknownFailure
}

public fun InstanceApiV1InstanceExtendedDescriptionGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceExtendedDescriptionGetClient = DefaultInstanceApiV1InstanceExtendedDescriptionGetClient(configuration)

public class DefaultInstanceApiV1InstanceExtendedDescriptionGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceExtendedDescriptionGetClient {
  override suspend fun getInstanceExtendedDescription(): InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/extended_description") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponseSuccess(response.body<ExtendedDescription>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceExtendedDescriptionGetClient.GetInstanceExtendedDescriptionResponseUnknownFailure(500)
    }
  }
}
