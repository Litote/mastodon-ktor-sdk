package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdRebloggedByGet.client

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

public class StatusesApiV1StatusesIdRebloggedByGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * See who boosted a status
   */
  public suspend fun getStatusRebloggedBy(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    sinceId: String? = null,
  ): GetStatusRebloggedByResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/reblogged_by".replace("/{id}", "/${id.encodeURLPathPart()}")) {
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
        200 -> GetStatusRebloggedByResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetStatusRebloggedByResponseFailure401(response.body<Error>())
        410 -> GetStatusRebloggedByResponseFailure410
        422 -> GetStatusRebloggedByResponseFailure(response.body<ValidationError>())
        else -> GetStatusRebloggedByResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusRebloggedByResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusRebloggedByResponse

  @Serializable
  public data class GetStatusRebloggedByResponseSuccess(
    public val body: List<Account>,
  ) : GetStatusRebloggedByResponse()

  @Serializable
  public data class GetStatusRebloggedByResponseFailure401(
    public val body: Error,
  ) : GetStatusRebloggedByResponse()

  @Serializable
  public object GetStatusRebloggedByResponseFailure410 : GetStatusRebloggedByResponse()

  @Serializable
  public data class GetStatusRebloggedByResponseFailure(
    public val body: ValidationError,
  ) : GetStatusRebloggedByResponse()

  @Serializable
  public data class GetStatusRebloggedByResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusRebloggedByResponse()
}
