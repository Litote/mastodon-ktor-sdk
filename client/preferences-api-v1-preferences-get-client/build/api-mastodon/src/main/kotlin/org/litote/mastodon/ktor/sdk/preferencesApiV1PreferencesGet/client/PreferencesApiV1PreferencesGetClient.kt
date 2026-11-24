package org.litote.mastodon.ktor.sdk.preferencesApiV1PreferencesGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface PreferencesApiV1PreferencesGetClient {
  /**
   * View user preferences
   */
  public suspend fun getPreferences(): GetPreferencesResponse

  @Serializable
  public sealed class GetPreferencesResponse : OperationResponse<Unit> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetPreferencesResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPreferencesResponse(),
      OperationResponseSuccess<Unit> {
    override val body: Unit
      get() = Unit

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
  public data class GetPreferencesResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPreferencesResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetPreferencesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPreferencesResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetPreferencesResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPreferencesResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetPreferencesResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetPreferencesResponse(),
      OperationResponseUnknownFailure
}

public fun PreferencesApiV1PreferencesGetClient(configuration: ClientConfiguration = defaultClientConfiguration): PreferencesApiV1PreferencesGetClient = DefaultPreferencesApiV1PreferencesGetClient(configuration)

public class DefaultPreferencesApiV1PreferencesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : PreferencesApiV1PreferencesGetClient {
  override suspend fun getPreferences(): PreferencesApiV1PreferencesGetClient.GetPreferencesResponse {
    try {
      val response = configuration.client.`get`("api/v1/preferences") {
      }
      return when (response.status.value) {
        200 -> PreferencesApiV1PreferencesGetClient.GetPreferencesResponseSuccess(response.headers)
        401, 404, 429, 503 -> PreferencesApiV1PreferencesGetClient.GetPreferencesResponseFailure401(response.body<Error>(), response.headers)
        410 -> PreferencesApiV1PreferencesGetClient.GetPreferencesResponseFailure410(response.headers)
        422 -> PreferencesApiV1PreferencesGetClient.GetPreferencesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> PreferencesApiV1PreferencesGetClient.GetPreferencesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PreferencesApiV1PreferencesGetClient.GetPreferencesResponseUnknownFailure(500)
    }
  }
}
