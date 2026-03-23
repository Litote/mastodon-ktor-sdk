package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a single status
   */
  public suspend fun getStatus(id: String): GetStatusResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetStatusResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> GetStatusResponseFailure401(response.body<Error>())
        410 -> GetStatusResponseFailure410
        422 -> GetStatusResponseFailure(response.body<ValidationError>())
        else -> GetStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusResponse

  @Serializable
  public data class GetStatusResponseSuccess(
    public val body: Status,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseFailure401(
    public val body: Error,
  ) : GetStatusResponse()

  @Serializable
  public object GetStatusResponseFailure410 : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseFailure(
    public val body: ValidationError,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusResponse()
}
