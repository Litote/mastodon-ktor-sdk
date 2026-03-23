package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdIdentityProofsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.IdentityProof
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class AccountsApiV1AccountsIdIdentityProofsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Identity proofs
   */
  public suspend fun getAccountIdentityProofs(id: String): GetAccountIdentityProofsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/identity_proofs".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetAccountIdentityProofsResponseSuccess(response.body<List<IdentityProof>>())
        401, 404, 422, 429, 503 -> GetAccountIdentityProofsResponseFailure401(response.body<Error>())
        410 -> GetAccountIdentityProofsResponseFailure
        else -> GetAccountIdentityProofsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountIdentityProofsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountIdentityProofsResponse

  @Serializable
  public data class GetAccountIdentityProofsResponseSuccess(
    public val body: List<IdentityProof>,
  ) : GetAccountIdentityProofsResponse()

  @Serializable
  public data class GetAccountIdentityProofsResponseFailure401(
    public val body: Error,
  ) : GetAccountIdentityProofsResponse()

  @Serializable
  public object GetAccountIdentityProofsResponseFailure : GetAccountIdentityProofsResponse()

  @Serializable
  public data class GetAccountIdentityProofsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountIdentityProofsResponse()
}
