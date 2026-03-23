package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdHistoryGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.StatusEdit
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class StatusesApiV1StatusesIdHistoryGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View edit history of a status
   */
  public suspend fun getStatusHistory(id: String): GetStatusHistoryResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/history".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetStatusHistoryResponseSuccess(response.body<List<StatusEdit>>())
        401, 404, 429, 503 -> GetStatusHistoryResponseFailure401(response.body<Error>())
        410 -> GetStatusHistoryResponseFailure410
        422 -> GetStatusHistoryResponseFailure(response.body<ValidationError>())
        else -> GetStatusHistoryResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusHistoryResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusHistoryResponse

  @Serializable
  public data class GetStatusHistoryResponseSuccess(
    public val body: List<StatusEdit>,
  ) : GetStatusHistoryResponse()

  @Serializable
  public data class GetStatusHistoryResponseFailure401(
    public val body: Error,
  ) : GetStatusHistoryResponse()

  @Serializable
  public object GetStatusHistoryResponseFailure410 : GetStatusHistoryResponse()

  @Serializable
  public data class GetStatusHistoryResponseFailure(
    public val body: ValidationError,
  ) : GetStatusHistoryResponse()

  @Serializable
  public data class GetStatusHistoryResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusHistoryResponse()
}
