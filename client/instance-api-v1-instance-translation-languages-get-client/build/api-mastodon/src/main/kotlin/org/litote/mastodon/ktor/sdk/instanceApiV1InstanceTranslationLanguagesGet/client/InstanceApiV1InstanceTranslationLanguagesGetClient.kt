package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceTranslationLanguagesGet.client

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

public interface InstanceApiV1InstanceTranslationLanguagesGetClient {
  /**
   * View translation languages
   */
  public suspend fun getInstanceTranslationLanguages(): GetInstanceTranslationLanguagesResponse

  @Serializable
  public sealed class GetInstanceTranslationLanguagesResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTranslationLanguagesResponse() {
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
  public data class GetInstanceTranslationLanguagesResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTranslationLanguagesResponse()

  @Serializable
  public data class GetInstanceTranslationLanguagesResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetInstanceTranslationLanguagesResponse()
}

public fun InstanceApiV1InstanceTranslationLanguagesGetClient(configuration: ClientConfiguration = defaultClientConfiguration): InstanceApiV1InstanceTranslationLanguagesGetClient = DefaultInstanceApiV1InstanceTranslationLanguagesGetClient(configuration)

public class DefaultInstanceApiV1InstanceTranslationLanguagesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : InstanceApiV1InstanceTranslationLanguagesGetClient {
  override suspend fun getInstanceTranslationLanguages(): InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/translation_languages") {
      }
      return when (response.status.value) {
        200 -> InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponseSuccess(response.headers)
        401, 404, 429, 503 -> InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponseFailure401(response.body<Error>(), response.headers)
        410 -> InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponseFailure410(response.headers)
        422 -> InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return InstanceApiV1InstanceTranslationLanguagesGetClient.GetInstanceTranslationLanguagesResponseUnknownFailure(500)
    }
  }
}
