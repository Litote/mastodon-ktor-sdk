package org.litote.mastodon.ktor.sdk.blocksApiV1BlocksGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
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

public class BlocksApiV1BlocksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View blocked users
   */
  public suspend fun getBlocks(
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetBlocksResponse {
    try {
      val response = configuration.client.`get`("api/v1/blocks") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetBlocksResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetBlocksResponseFailure401(response.body<Error>())
        410 -> GetBlocksResponseFailure410
        422 -> GetBlocksResponseFailure(response.body<ValidationError>())
        else -> GetBlocksResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetBlocksResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetBlocksResponse

  @Serializable
  public data class GetBlocksResponseSuccess(
    public val body: List<Account>,
  ) : GetBlocksResponse()

  @Serializable
  public data class GetBlocksResponseFailure401(
    public val body: Error,
  ) : GetBlocksResponse()

  @Serializable
  public object GetBlocksResponseFailure410 : GetBlocksResponse()

  @Serializable
  public data class GetBlocksResponseFailure(
    public val body: ValidationError,
  ) : GetBlocksResponse()

  @Serializable
  public data class GetBlocksResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetBlocksResponse()
}
