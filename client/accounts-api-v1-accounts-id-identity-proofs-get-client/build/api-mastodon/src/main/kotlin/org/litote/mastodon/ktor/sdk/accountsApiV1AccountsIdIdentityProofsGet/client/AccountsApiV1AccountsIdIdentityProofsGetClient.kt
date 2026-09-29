package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdIdentityProofsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.IdentityProof
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public interface AccountsApiV1AccountsIdIdentityProofsGetClient {
  /**
   * Identity proofs
   */
  public suspend fun getAccountIdentityProofs(id: String): GetAccountIdentityProofsResponse

  @Serializable
  public sealed class GetAccountIdentityProofsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountIdentityProofsResponseSuccess(
    public val body: List<IdentityProof>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountIdentityProofsResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class GetAccountIdentityProofsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountIdentityProofsResponse()

  @Serializable
  public data class GetAccountIdentityProofsResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountIdentityProofsResponse()

  @Serializable
  public data class GetAccountIdentityProofsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountIdentityProofsResponse()
}

public fun AccountsApiV1AccountsIdIdentityProofsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdIdentityProofsGetClient = DefaultAccountsApiV1AccountsIdIdentityProofsGetClient(configuration)

public class DefaultAccountsApiV1AccountsIdIdentityProofsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdIdentityProofsGetClient {
  override suspend fun getAccountIdentityProofs(id: String): AccountsApiV1AccountsIdIdentityProofsGetClient.GetAccountIdentityProofsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/identity_proofs".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdIdentityProofsGetClient.GetAccountIdentityProofsResponseSuccess(response.body<List<IdentityProof>>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdIdentityProofsGetClient.GetAccountIdentityProofsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdIdentityProofsGetClient.GetAccountIdentityProofsResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdIdentityProofsGetClient.GetAccountIdentityProofsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdIdentityProofsGetClient.GetAccountIdentityProofsResponseUnknownFailure(500)
    }
  }
}
