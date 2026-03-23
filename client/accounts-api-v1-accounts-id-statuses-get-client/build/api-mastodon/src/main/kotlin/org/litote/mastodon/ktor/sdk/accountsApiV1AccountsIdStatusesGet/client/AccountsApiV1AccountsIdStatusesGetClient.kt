package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdStatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
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

public class AccountsApiV1AccountsIdStatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get account's statuses
   */
  public suspend fun getAccountStatuses(
    id: String,
    excludeReblogs: Boolean? = null,
    excludeReplies: Boolean? = null,
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    onlyMedia: Boolean? = null,
    pinned: Boolean? = null,
    sinceId: String? = null,
    tagged: String? = null,
  ): GetAccountStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/statuses".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (excludeReblogs != null) {
            parameters.append("exclude_reblogs", excludeReblogs.toString())
          }
          if (excludeReplies != null) {
            parameters.append("exclude_replies", excludeReplies.toString())
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (onlyMedia != null) {
            parameters.append("only_media", onlyMedia.toString())
          }
          if (pinned != null) {
            parameters.append("pinned", pinned.toString())
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
          if (tagged != null) {
            parameters.append("tagged", tagged)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetAccountStatusesResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetAccountStatusesResponseFailure401(response.body<Error>())
        410 -> GetAccountStatusesResponseFailure410
        422 -> GetAccountStatusesResponseFailure(response.body<ValidationError>())
        else -> GetAccountStatusesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountStatusesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountStatusesResponse

  @Serializable
  public data class GetAccountStatusesResponseSuccess(
    public val body: List<Status>,
  ) : GetAccountStatusesResponse()

  @Serializable
  public data class GetAccountStatusesResponseFailure401(
    public val body: Error,
  ) : GetAccountStatusesResponse()

  @Serializable
  public object GetAccountStatusesResponseFailure410 : GetAccountStatusesResponse()

  @Serializable
  public data class GetAccountStatusesResponseFailure(
    public val body: ValidationError,
  ) : GetAccountStatusesResponse()

  @Serializable
  public data class GetAccountStatusesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountStatusesResponse()
}
