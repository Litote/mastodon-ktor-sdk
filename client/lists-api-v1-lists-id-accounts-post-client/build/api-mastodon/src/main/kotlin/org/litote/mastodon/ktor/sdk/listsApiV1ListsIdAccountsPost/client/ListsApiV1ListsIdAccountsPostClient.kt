package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdAccountsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
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

public class ListsApiV1ListsIdAccountsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Add accounts to a list
   */
  public suspend fun postListAccounts(request: PostListAccountsRequest, id: String): PostListAccountsResponse {
    try {
      val response = configuration.client.post("api/v1/lists/{id}/accounts".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostListAccountsResponseSuccess
        401, 404, 422, 429, 503 -> PostListAccountsResponseFailure401(response.body<Error>())
        410 -> PostListAccountsResponseFailure
        else -> PostListAccountsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostListAccountsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostListAccountsRequest(
    @SerialName("account_ids")
    public val accountIds: List<String>,
  )

  @Serializable
  public sealed class PostListAccountsResponse

  @Serializable
  public object PostListAccountsResponseSuccess : PostListAccountsResponse()

  @Serializable
  public data class PostListAccountsResponseFailure401(
    public val body: Error,
  ) : PostListAccountsResponse()

  @Serializable
  public object PostListAccountsResponseFailure : PostListAccountsResponse()

  @Serializable
  public data class PostListAccountsResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostListAccountsResponse()
}
