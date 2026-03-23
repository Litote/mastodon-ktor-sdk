package org.litote.mastodon.ktor.sdk.domainBlocksApiV1DomainBlocksGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class DomainBlocksApiV1DomainBlocksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get domain blocks
   */
  public suspend fun getDomainBlocks(
    limit: Long? = 100,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetDomainBlocksResponse {
    try {
      val response = configuration.client.`get`("api/v1/domain_blocks") {
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
        200 -> GetDomainBlocksResponseSuccess(response.body<List<String>>())
        401, 404, 429, 503 -> GetDomainBlocksResponseFailure401(response.body<Error>())
        410 -> GetDomainBlocksResponseFailure410
        422 -> GetDomainBlocksResponseFailure(response.body<ValidationError>())
        else -> GetDomainBlocksResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetDomainBlocksResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetDomainBlocksResponse

  @Serializable
  public data class GetDomainBlocksResponseSuccess(
    public val body: List<String>,
  ) : GetDomainBlocksResponse()

  @Serializable
  public data class GetDomainBlocksResponseFailure401(
    public val body: Error,
  ) : GetDomainBlocksResponse()

  @Serializable
  public object GetDomainBlocksResponseFailure410 : GetDomainBlocksResponse()

  @Serializable
  public data class GetDomainBlocksResponseFailure(
    public val body: ValidationError,
  ) : GetDomainBlocksResponse()

  @Serializable
  public data class GetDomainBlocksResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetDomainBlocksResponse()
}
