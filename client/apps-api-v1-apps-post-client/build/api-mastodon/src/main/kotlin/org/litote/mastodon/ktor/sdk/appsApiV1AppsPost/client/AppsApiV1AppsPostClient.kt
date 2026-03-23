package org.litote.mastodon.ktor.sdk.appsApiV1AppsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.CredentialApplication
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class AppsApiV1AppsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Create an application
   */
  public suspend fun createApp(request: CreateAppRequest): CreateAppResponse {
    try {
      val response = configuration.client.post("api/v1/apps") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateAppResponseSuccess(response.body<CredentialApplication>())
        401, 404, 422, 429, 503 -> CreateAppResponseFailure401(response.body<Error>())
        410 -> CreateAppResponseFailure
        else -> CreateAppResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateAppResponseUnknownFailure(500)
    }
  }

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
  public sealed class CreateAppResponse

  @Serializable
  public data class CreateAppResponseSuccess(
    public val body: CredentialApplication,
  ) : CreateAppResponse()

  @Serializable
  public data class CreateAppResponseFailure401(
    public val body: Error,
  ) : CreateAppResponse()

  @Serializable
  public object CreateAppResponseFailure : CreateAppResponse()

  @Serializable
  public data class CreateAppResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateAppResponse()
}
