package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceRulesGet.client

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
import org.litote.mastodon.ktor.sdk.sharedInstanceapiv1instanceget2050c4cc.model.Rule

public interface InstanceApiV1InstanceRulesGetClient {
  /**
   * List of rules
   */
  public suspend fun getInstanceRules(): GetInstanceRulesResponse

  @Serializable
  public sealed class GetInstanceRulesResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstanceRulesResponseSuccess(
    public val body: List<Rule>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceRulesResponse() {
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
  public data class GetInstanceRulesResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceRulesResponse()

  @Serializable
  public data class GetInstanceRulesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceRulesResponse()

  @Serializable
  public data class GetInstanceRulesResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceRulesResponse()

  @Serializable
  public data class GetInstanceRulesResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceRulesResponse()
}

public fun InstanceApiV1InstanceRulesGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceRulesGetClient = DefaultInstanceApiV1InstanceRulesGetClient(configuration)

public class DefaultInstanceApiV1InstanceRulesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceRulesGetClient {
  override suspend fun getInstanceRules(): InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/rules") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponseSuccess(response.body<List<Rule>>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceRulesGetClient.GetInstanceRulesResponseUnknownFailure(500)
    }
  }
}
