package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdQuotesQuotingStatusIdRevokePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Revoke a quote post
   */
  public suspend fun postStatusesByIdQuotesByQuotingStatusIdRevoke(id: String, quotingStatusId: String): PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/quotes/{quoting_status_id}/revoke".replace("/{id}", "/${id.encodeURLPathPart()}").replace("/{quoting_status_id}", "/${quotingStatusId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseSuccess(response.body<Status>())
        401, 403, 404, 429, 503 -> PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure401(response.body<Error>())
        410 -> PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure410
        422 -> PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure(response.body<ValidationError>())
        else -> PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseSuccess(
    public val body: Status,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure401(
    public val body: Error,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public object PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure410 : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure(
    public val body: ValidationError,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()
}
