package org.litote.mastodon.ktor.sdk.appsApiV1AppsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.CredentialApplication
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public interface AppsApiV1AppsPostClient {
  /**
   * Create an application
   */
  public suspend fun createApp(request: CreateAppRequest): CreateAppResponse

  @Serializable
  public data class CreateAppRequest(
    @SerialName("client_name")
    public val clientName: String,
    @SerialName("redirect_uris")
    public val redirectUris: List<String>,
    public val scopes: String? = "read",
    public val website: String? = null,
  )

  @Serializable
  public sealed class CreateAppResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateAppResponseSuccess(
    public val body: CredentialApplication,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAppResponse() {
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
  public data class CreateAppResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAppResponse()

  @Serializable
  public data class CreateAppResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAppResponse()

  @Serializable
  public data class CreateAppResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAppResponse()
}

public fun AppsApiV1AppsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AppsApiV1AppsPostClient = DefaultAppsApiV1AppsPostClient(configuration)

public class DefaultAppsApiV1AppsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AppsApiV1AppsPostClient {
  override suspend fun createApp(request: AppsApiV1AppsPostClient.CreateAppRequest): AppsApiV1AppsPostClient.CreateAppResponse {
    try {
      val response = configuration.client.post("api/v1/apps") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> AppsApiV1AppsPostClient.CreateAppResponseSuccess(response.body<CredentialApplication>(), response.headers)
        401, 404, 422, 429, 503 -> AppsApiV1AppsPostClient.CreateAppResponseFailure401(response.body<Error>(), response.headers)
        410 -> AppsApiV1AppsPostClient.CreateAppResponseFailure(response.headers)
        else -> AppsApiV1AppsPostClient.CreateAppResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AppsApiV1AppsPostClient.CreateAppResponseUnknownFailure(500)
    }
  }
}
