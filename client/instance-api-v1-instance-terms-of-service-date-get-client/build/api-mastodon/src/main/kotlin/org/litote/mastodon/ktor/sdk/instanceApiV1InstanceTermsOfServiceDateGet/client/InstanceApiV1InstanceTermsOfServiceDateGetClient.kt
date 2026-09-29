package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceTermsOfServiceDateGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
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

public interface InstanceApiV1InstanceTermsOfServiceDateGetClient {
  /**
   * View a specific version of the terms of service
   */
  public suspend fun getInstanceTermsOfServiceByDate(date: String): GetInstanceTermsOfServiceByDateResponse

  @Serializable
  public sealed class GetInstanceTermsOfServiceByDateResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseSuccess(
    public val body: TermsOfService,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceByDateResponse() {
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
  public data class GetInstanceTermsOfServiceByDateResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceByDateResponse()

  @Serializable
  public data class GetInstanceTermsOfServiceByDateResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTermsOfServiceByDateResponse()
}

public fun InstanceApiV1InstanceTermsOfServiceDateGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceTermsOfServiceDateGetClient = DefaultInstanceApiV1InstanceTermsOfServiceDateGetClient(configuration)

public class DefaultInstanceApiV1InstanceTermsOfServiceDateGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceTermsOfServiceDateGetClient {
  override suspend fun getInstanceTermsOfServiceByDate(date: String): InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/terms_of_service/{date}".replace("/{date}", "/${date.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponseSuccess(response.body<TermsOfService>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceTermsOfServiceDateGetClient.GetInstanceTermsOfServiceByDateResponseUnknownFailure(500)
    }
  }
}
