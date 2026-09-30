package org.litote.mastodon.ktor.sdk.customEmojisApiV1CustomEmojisGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget10fddec7.model.CustomEmoji
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface CustomEmojisApiV1CustomEmojisGetClient {
  /**
   * View all custom emoji
   */
  public suspend fun getCustomEmojis(): GetCustomEmojisResponse

  @Serializable
  public sealed class GetCustomEmojisResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetCustomEmojisResponseSuccess(
    public val body: List<CustomEmoji>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetCustomEmojisResponse() {
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
  public data class GetCustomEmojisResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetCustomEmojisResponse()

  @Serializable
  public data class GetCustomEmojisResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetCustomEmojisResponse()

  @Serializable
  public data class GetCustomEmojisResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetCustomEmojisResponse()

  @Serializable
  public data class GetCustomEmojisResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetCustomEmojisResponse()
}

public fun CustomEmojisApiV1CustomEmojisGetClient(configuration: ClientConfiguration = defaultClientConfiguration): CustomEmojisApiV1CustomEmojisGetClient = DefaultCustomEmojisApiV1CustomEmojisGetClient(configuration)

public class DefaultCustomEmojisApiV1CustomEmojisGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : CustomEmojisApiV1CustomEmojisGetClient {
  override suspend fun getCustomEmojis(): CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponse {
    try {
      val response = configuration.client.`get`("api/v1/custom_emojis") {
      }
      return when (response.status.value) {
        200 -> CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponseSuccess(response.body<List<CustomEmoji>>(), response.headers)
        401, 404, 429, 503 -> CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponseFailure401(response.body<Error>(), response.headers)
        410 -> CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponseFailure410(response.headers)
        422 -> CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponseFailure(response.body<ValidationError>(), response.headers)
        else -> CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CustomEmojisApiV1CustomEmojisGetClient.GetCustomEmojisResponseUnknownFailure(500)
    }
  }
}
