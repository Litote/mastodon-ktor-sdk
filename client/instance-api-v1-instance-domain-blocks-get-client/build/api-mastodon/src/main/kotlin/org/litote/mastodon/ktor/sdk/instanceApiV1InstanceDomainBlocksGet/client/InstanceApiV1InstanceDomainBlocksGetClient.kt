package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceDomainBlocksGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.DomainBlock
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class InstanceApiV1InstanceDomainBlocksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View moderated servers
   */
  public suspend fun getInstanceDomainBlocks(): GetInstanceDomainBlocksResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/domain_blocks") {
      }
      return when (response.status.value) {
        200 -> GetInstanceDomainBlocksResponseSuccess(response.body<List<DomainBlock>>())
        401, 404, 429, 503 -> GetInstanceDomainBlocksResponseFailure401(response.body<Error>())
        410 -> GetInstanceDomainBlocksResponseFailure410
        422 -> GetInstanceDomainBlocksResponseFailure(response.body<ValidationError>())
        else -> GetInstanceDomainBlocksResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceDomainBlocksResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceDomainBlocksResponse

  @Serializable
  public data class GetInstanceDomainBlocksResponseSuccess(
    public val body: List<DomainBlock>,
  ) : GetInstanceDomainBlocksResponse()

  @Serializable
  public data class GetInstanceDomainBlocksResponseFailure401(
    public val body: Error,
  ) : GetInstanceDomainBlocksResponse()

  @Serializable
  public object GetInstanceDomainBlocksResponseFailure410 : GetInstanceDomainBlocksResponse()

  @Serializable
  public data class GetInstanceDomainBlocksResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceDomainBlocksResponse()

  @Serializable
  public data class GetInstanceDomainBlocksResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceDomainBlocksResponse()
}
