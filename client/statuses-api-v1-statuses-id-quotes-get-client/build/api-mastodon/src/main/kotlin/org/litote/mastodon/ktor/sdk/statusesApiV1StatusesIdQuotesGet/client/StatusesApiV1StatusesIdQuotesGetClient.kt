package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdQuotesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdQuotesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * See quotes of a status
   */
  public suspend fun getStatusQuotes(
    id: String,
    limit: Long? = 20,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetStatusQuotesResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/quotes".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetStatusQuotesResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetStatusQuotesResponseFailure401(response.body<Error>())
        410 -> GetStatusQuotesResponseFailure410
        422 -> GetStatusQuotesResponseFailure(response.body<ValidationError>())
        else -> GetStatusQuotesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusQuotesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusQuotesResponse

  @Serializable
  public data class GetStatusQuotesResponseSuccess(
    public val body: List<Status>,
  ) : GetStatusQuotesResponse()

  @Serializable
  public data class GetStatusQuotesResponseFailure401(
    public val body: Error,
  ) : GetStatusQuotesResponse()

  @Serializable
  public object GetStatusQuotesResponseFailure410 : GetStatusQuotesResponse()

  @Serializable
  public data class GetStatusQuotesResponseFailure(
    public val body: ValidationError,
  ) : GetStatusQuotesResponse()

  @Serializable
  public data class GetStatusQuotesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusQuotesResponse()
}
