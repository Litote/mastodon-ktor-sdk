package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceTermsOfServiceGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedInstanceapiv1instancetermsofservicedategetFcd3eaa4.model.TermsOfService

public interface InstanceApiV1InstanceTermsOfServiceGetClient {
  /**
   * View terms of service
   */
  public suspend fun getInstanceTermsOfService(): GetInstanceTermsOfServiceResponse

  @Serializable
  public sealed class GetInstanceTermsOfServiceResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstanceTermsOfServiceResponseSuccess(
    public val body: TermsOfService,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceResponse() {
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
  public data class GetInstanceTermsOfServiceResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceResponse()
}

public fun InstanceApiV1InstanceTermsOfServiceGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceTermsOfServiceGetClient = DefaultInstanceApiV1InstanceTermsOfServiceGetClient(configuration)

public class DefaultInstanceApiV1InstanceTermsOfServiceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceTermsOfServiceGetClient {
  override suspend fun getInstanceTermsOfService(): InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/terms_of_service") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponseSuccess(response.body<TermsOfService>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceTermsOfServiceGetClient.GetInstanceTermsOfServiceResponseUnknownFailure(500)
    }
  }
}
