package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsRelationshipsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public interface AccountsApiV1AccountsRelationshipsGetClient {
  /**
   * Check relationships to other accounts
   */
  public suspend fun getAccountRelationships(id: List<String>? = null, withSuspended: Boolean? = false): GetAccountRelationshipsResponse

  @Serializable
  public sealed class GetAccountRelationshipsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountRelationshipsResponseSuccess(
    public val body: List<Relationship>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountRelationshipsResponse() {
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
  public data class GetAccountRelationshipsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountRelationshipsResponse()

  @Serializable
  public data class GetAccountRelationshipsResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountRelationshipsResponse()

  @Serializable
  public data class GetAccountRelationshipsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountRelationshipsResponse()
}

public fun AccountsApiV1AccountsRelationshipsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsRelationshipsGetClient = DefaultAccountsApiV1AccountsRelationshipsGetClient(configuration)

public class DefaultAccountsApiV1AccountsRelationshipsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsRelationshipsGetClient {
  override suspend fun getAccountRelationships(id: List<String>?, withSuspended: Boolean?): AccountsApiV1AccountsRelationshipsGetClient.GetAccountRelationshipsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/relationships") {
        url {
          if (id != null) {
            parameters.appendAll("id", id)
          }
          if (withSuspended != null) {
            parameters.append("with_suspended", withSuspended.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsRelationshipsGetClient.GetAccountRelationshipsResponseSuccess(response.body<List<Relationship>>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsRelationshipsGetClient.GetAccountRelationshipsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsRelationshipsGetClient.GetAccountRelationshipsResponseFailure(response.headers)
        else -> AccountsApiV1AccountsRelationshipsGetClient.GetAccountRelationshipsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsRelationshipsGetClient.GetAccountRelationshipsResponseUnknownFailure(500)
    }
  }
}
