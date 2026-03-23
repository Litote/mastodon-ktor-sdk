package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsRelationshipsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsRelationshipsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Check relationships to other accounts
   */
  public suspend fun getAccountRelationships(id: List<String>? = null, withSuspended: Boolean? = false): GetAccountRelationshipsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/relationships") {
        url {
          if (id != null) {
            parameters.append("id", id.joinToString(","))
          }
          if (withSuspended != null) {
            parameters.append("with_suspended", withSuspended.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> GetAccountRelationshipsResponseSuccess(response.body<List<Relationship>>())
        401, 404, 422, 429, 503 -> GetAccountRelationshipsResponseFailure401(response.body<Error>())
        410 -> GetAccountRelationshipsResponseFailure
        else -> GetAccountRelationshipsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountRelationshipsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object Id

  @Serializable
  public sealed class GetAccountRelationshipsResponse

  @Serializable
  public data class GetAccountRelationshipsResponseSuccess(
    public val body: List<Relationship>,
  ) : GetAccountRelationshipsResponse()

  @Serializable
  public data class GetAccountRelationshipsResponseFailure401(
    public val body: Error,
  ) : GetAccountRelationshipsResponse()

  @Serializable
  public object GetAccountRelationshipsResponseFailure : GetAccountRelationshipsResponse()

  @Serializable
  public data class GetAccountRelationshipsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountRelationshipsResponse()
}
