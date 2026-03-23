package org.litote.mastodon.ktor.sdk.domainBlocksApiV1DomainBlocksDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class DomainBlocksApiV1DomainBlocksDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unblock a domain
   */
  public suspend fun deleteDomainBlocks(request: DeleteDomainBlocksRequest): DeleteDomainBlocksResponse {
    try {
      val response = configuration.client.delete("api/v1/domain_blocks") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> DeleteDomainBlocksResponseSuccess
        401, 404, 422, 429, 503 -> DeleteDomainBlocksResponseFailure401(response.body<Error>())
        410 -> DeleteDomainBlocksResponseFailure
        else -> DeleteDomainBlocksResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteDomainBlocksResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class DeleteDomainBlocksRequest(
    public val domain: String,
  )

  @Serializable
  public sealed class DeleteDomainBlocksResponse

  @Serializable
  public object DeleteDomainBlocksResponseSuccess : DeleteDomainBlocksResponse()

  @Serializable
  public data class DeleteDomainBlocksResponseFailure401(
    public val body: Error,
  ) : DeleteDomainBlocksResponse()

  @Serializable
  public object DeleteDomainBlocksResponseFailure : DeleteDomainBlocksResponse()

  @Serializable
  public data class DeleteDomainBlocksResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteDomainBlocksResponse()
}
