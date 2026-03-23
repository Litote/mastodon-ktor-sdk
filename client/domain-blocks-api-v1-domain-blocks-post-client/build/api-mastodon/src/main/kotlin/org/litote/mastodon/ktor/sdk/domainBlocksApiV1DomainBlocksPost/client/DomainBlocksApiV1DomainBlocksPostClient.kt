package org.litote.mastodon.ktor.sdk.domainBlocksApiV1DomainBlocksPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class DomainBlocksApiV1DomainBlocksPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Block a domain
   */
  public suspend fun createDomainBlock(request: CreateDomainBlockRequest): CreateDomainBlockResponse {
    try {
      val response = configuration.client.post("api/v1/domain_blocks") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateDomainBlockResponseSuccess
        401, 404, 422, 429, 503 -> CreateDomainBlockResponseFailure401(response.body<Error>())
        410 -> CreateDomainBlockResponseFailure
        else -> CreateDomainBlockResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateDomainBlockResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateDomainBlockRequest(
    public val domain: String,
  )

  @Serializable
  public sealed class CreateDomainBlockResponse

  @Serializable
  public object CreateDomainBlockResponseSuccess : CreateDomainBlockResponse()

  @Serializable
  public data class CreateDomainBlockResponseFailure401(
    public val body: Error,
  ) : CreateDomainBlockResponse()

  @Serializable
  public object CreateDomainBlockResponseFailure : CreateDomainBlockResponse()

  @Serializable
  public data class CreateDomainBlockResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateDomainBlockResponse()
}
