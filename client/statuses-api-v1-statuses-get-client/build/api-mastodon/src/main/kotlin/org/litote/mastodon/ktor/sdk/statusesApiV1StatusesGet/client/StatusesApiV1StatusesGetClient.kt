package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View multiple statuses
   */
  public suspend fun getStatuses(id: List<String>? = null): GetStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses") {
        url {
          if (id != null) {
            parameters.append("id", id.joinToString(","))
          }
        }
      }
      return when (response.status.value) {
        200 -> GetStatusesResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetStatusesResponseFailure401(response.body<Error>())
        410 -> GetStatusesResponseFailure410
        422 -> GetStatusesResponseFailure(response.body<ValidationError>())
        else -> GetStatusesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object Id

  @Serializable
  public sealed class GetStatusesResponse

  @Serializable
  public data class GetStatusesResponseSuccess(
    public val body: List<Status>,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseFailure401(
    public val body: Error,
  ) : GetStatusesResponse()

  @Serializable
  public object GetStatusesResponseFailure410 : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseFailure(
    public val body: ValidationError,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusesResponse()
}
