package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceActivityGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface InstanceApiV1InstanceActivityGetClient {
  /**
   * Weekly activity
   */
  public suspend fun getInstanceActivity(): GetInstanceActivityResponse

  @Serializable
  public sealed class GetInstanceActivityResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstanceActivityResponseSuccess(
    public val body: List<JsonElement>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceActivityResponse() {
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
  public data class GetInstanceActivityResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceActivityResponse()

  @Serializable
  public data class GetInstanceActivityResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceActivityResponse()

  @Serializable
  public data class GetInstanceActivityResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceActivityResponse()

  @Serializable
  public data class GetInstanceActivityResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceActivityResponse()
}

public fun InstanceApiV1InstanceActivityGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceActivityGetClient = DefaultInstanceApiV1InstanceActivityGetClient(configuration)

public class DefaultInstanceApiV1InstanceActivityGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceActivityGetClient {
  override suspend fun getInstanceActivity(): InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/activity") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponseSuccess(response.body<List<JsonElement>>(), response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceActivityGetClient.GetInstanceActivityResponseUnknownFailure(500)
    }
  }
}
