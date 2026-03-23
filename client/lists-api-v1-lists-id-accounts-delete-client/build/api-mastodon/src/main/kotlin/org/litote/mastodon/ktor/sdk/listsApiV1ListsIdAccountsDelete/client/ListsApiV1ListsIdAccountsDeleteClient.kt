package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdAccountsDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class ListsApiV1ListsIdAccountsDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove accounts from list
   */
  public suspend fun deleteListAccounts(request: DeleteListAccountsRequest, id: String): DeleteListAccountsResponse {
    try {
      val response = configuration.client.delete("api/v1/lists/{id}/accounts".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> DeleteListAccountsResponseSuccess
        401, 404, 429, 503 -> DeleteListAccountsResponseFailure401(response.body<Error>())
        410 -> DeleteListAccountsResponseFailure410
        422 -> DeleteListAccountsResponseFailure(response.body<ValidationError>())
        else -> DeleteListAccountsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteListAccountsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class DeleteListAccountsRequest(
    @SerialName("account_ids")
    public val accountIds: List<String>,
  )

  @Serializable
  public sealed class DeleteListAccountsResponse

  @Serializable
  public object DeleteListAccountsResponseSuccess : DeleteListAccountsResponse()

  @Serializable
  public data class DeleteListAccountsResponseFailure401(
    public val body: Error,
  ) : DeleteListAccountsResponse()

  @Serializable
  public object DeleteListAccountsResponseFailure410 : DeleteListAccountsResponse()

  @Serializable
  public data class DeleteListAccountsResponseFailure(
    public val body: ValidationError,
  ) : DeleteListAccountsResponse()

  @Serializable
  public data class DeleteListAccountsResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteListAccountsResponse()
}
