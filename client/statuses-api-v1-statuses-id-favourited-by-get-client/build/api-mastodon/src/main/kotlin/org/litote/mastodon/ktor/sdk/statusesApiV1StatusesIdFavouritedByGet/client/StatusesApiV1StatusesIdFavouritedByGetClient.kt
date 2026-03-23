package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdFavouritedByGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class StatusesApiV1StatusesIdFavouritedByGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * See who favourited a status
   */
  public suspend fun getStatusFavouritedBy(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetStatusFavouritedByResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/favourited_by".replace("/{id}", "/${id.encodeURLPathPart()}")) {
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
        200 -> GetStatusFavouritedByResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetStatusFavouritedByResponseFailure401(response.body<Error>())
        410 -> GetStatusFavouritedByResponseFailure410
        422 -> GetStatusFavouritedByResponseFailure(response.body<ValidationError>())
        else -> GetStatusFavouritedByResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusFavouritedByResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusFavouritedByResponse

  @Serializable
  public data class GetStatusFavouritedByResponseSuccess(
    public val body: List<Account>,
  ) : GetStatusFavouritedByResponse()

  @Serializable
  public data class GetStatusFavouritedByResponseFailure401(
    public val body: Error,
  ) : GetStatusFavouritedByResponse()

  @Serializable
  public object GetStatusFavouritedByResponseFailure410 : GetStatusFavouritedByResponse()

  @Serializable
  public data class GetStatusFavouritedByResponseFailure(
    public val body: ValidationError,
  ) : GetStatusFavouritedByResponse()

  @Serializable
  public data class GetStatusFavouritedByResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusFavouritedByResponse()
}
