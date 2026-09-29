package org.litote.mastodon.ktor.sdk.instanceApiV1InstancePrivacyPolicyGet.client

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
import org.litote.mastodon.ktor.sdk.model.PrivacyPolicy
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV1InstancePrivacyPolicyGetClient {
  /**
   * View privacy policy
   */
  public suspend fun getInstancePrivacyPolicy(): GetInstancePrivacyPolicyResponse

  @Serializable
  public sealed class GetInstancePrivacyPolicyResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstancePrivacyPolicyResponseSuccess(
    public val body: PrivacyPolicy,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePrivacyPolicyResponse() {
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
  public data class GetInstancePrivacyPolicyResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePrivacyPolicyResponse()

  @Serializable
  public data class GetInstancePrivacyPolicyResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePrivacyPolicyResponse()

  @Serializable
  public data class GetInstancePrivacyPolicyResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePrivacyPolicyResponse()

  @Serializable
  public data class GetInstancePrivacyPolicyResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstancePrivacyPolicyResponse()
}

public fun InstanceApiV1InstancePrivacyPolicyGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstancePrivacyPolicyGetClient = DefaultInstanceApiV1InstancePrivacyPolicyGetClient(configuration)

public class DefaultInstanceApiV1InstancePrivacyPolicyGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstancePrivacyPolicyGetClient {
  override suspend fun getInstancePrivacyPolicy(): InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/privacy_policy") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponseSuccess(response.body<PrivacyPolicy>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponseFailure410(response.headers)
        422 -> InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstancePrivacyPolicyGetClient.GetInstancePrivacyPolicyResponseUnknownFailure(500)
    }
  }
}
