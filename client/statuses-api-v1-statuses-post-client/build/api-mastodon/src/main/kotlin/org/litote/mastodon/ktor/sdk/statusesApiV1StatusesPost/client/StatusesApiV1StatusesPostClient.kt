package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.CreateStatusRequest
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import io.ktor.client.request.`header` as setHeader

public class StatusesApiV1StatusesPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Post a new status
   */
  public suspend fun createStatus(request: CreateStatusRequest, idempotencyKey: JsonElement? = null): CreateStatusResponse {
    try {
      val response = configuration.client.post("api/v1/statuses") {
        if (idempotencyKey != null) {
          setHeader("Idempotency-Key", idempotencyKey)
        }
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateStatusResponseSuccess(response.body<org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.CreateStatusResponse>())
        401, 404, 422, 429, 503 -> CreateStatusResponseFailure401(response.body<Error>())
        410 -> CreateStatusResponseFailure
        else -> CreateStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object IdempotencyKey

  @Serializable
  public sealed class CreateStatusResponse

  @Serializable
  public data class CreateStatusResponseSuccess(
    public val body:
        org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.CreateStatusResponse,
  ) : CreateStatusResponse()

  @Serializable
  public data class CreateStatusResponseFailure401(
    public val body: Error,
  ) : CreateStatusResponse()

  @Serializable
  public object CreateStatusResponseFailure : CreateStatusResponse()

  @Serializable
  public data class CreateStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateStatusResponse()
}
