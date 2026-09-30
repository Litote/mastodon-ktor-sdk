package org.litote.mastodon.ktor.sdk.appsApiV1AppsVerifyCredentialsGet.client

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
import org.litote.mastodon.ktor.sdk.model.Application
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface AppsApiV1AppsVerifyCredentialsGetClient {
  /**
   * Verify your app works
   */
  public suspend fun getAppsVerifyCredentials(): GetAppsVerifyCredentialsResponse

  @Serializable
  public sealed class GetAppsVerifyCredentialsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAppsVerifyCredentialsResponseSuccess(
    public val body: Application,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAppsVerifyCredentialsResponse() {
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
  public data class GetAppsVerifyCredentialsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAppsVerifyCredentialsResponse()

  @Serializable
  public data class GetAppsVerifyCredentialsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAppsVerifyCredentialsResponse()

  @Serializable
  public data class GetAppsVerifyCredentialsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAppsVerifyCredentialsResponse()

  @Serializable
  public data class GetAppsVerifyCredentialsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAppsVerifyCredentialsResponse()
}

public fun AppsApiV1AppsVerifyCredentialsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AppsApiV1AppsVerifyCredentialsGetClient = DefaultAppsApiV1AppsVerifyCredentialsGetClient(configuration)

public class DefaultAppsApiV1AppsVerifyCredentialsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AppsApiV1AppsVerifyCredentialsGetClient {
  override suspend fun getAppsVerifyCredentials(): AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponse {
    try {
      val response = configuration.client.`get`("api/v1/apps/verify_credentials") {
      }
      return when (response.status.value) {
        200 -> AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponseSuccess(response.body<Application>(), response.headers)
        401, 404, 429, 503 -> AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponseFailure410(response.headers)
        422 -> AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AppsApiV1AppsVerifyCredentialsGetClient.GetAppsVerifyCredentialsResponseUnknownFailure(500)
    }
  }
}
