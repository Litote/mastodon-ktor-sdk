package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdSourceGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.StatusSource
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class StatusesApiV1StatusesIdSourceGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View status source
   */
  public suspend fun getStatusSource(id: String): GetStatusSourceResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/source".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetStatusSourceResponseSuccess(response.body<StatusSource>())
        401, 404, 429, 503 -> GetStatusSourceResponseFailure401(response.body<Error>())
        410 -> GetStatusSourceResponseFailure410
        422 -> GetStatusSourceResponseFailure(response.body<ValidationError>())
        else -> GetStatusSourceResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusSourceResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusSourceResponse

  @Serializable
  public data class GetStatusSourceResponseSuccess(
    public val body: StatusSource,
  ) : GetStatusSourceResponse()

  @Serializable
  public data class GetStatusSourceResponseFailure401(
    public val body: Error,
  ) : GetStatusSourceResponse()

  @Serializable
  public object GetStatusSourceResponseFailure410 : GetStatusSourceResponse()

  @Serializable
  public data class GetStatusSourceResponseFailure(
    public val body: ValidationError,
  ) : GetStatusSourceResponse()

  @Serializable
  public data class GetStatusSourceResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusSourceResponse()
}
