package org.litote.mastodon.ktor.sdk.listsApiV1ListsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.ListRepliesPolicyEnum

public class ListsApiV1ListsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Create a list
   */
  public suspend fun createList(request: CreateListRequest): CreateListResponse {
    try {
      val response = configuration.client.post("api/v1/lists") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateListResponseSuccess(response.body<List>())
        401, 404, 422, 429, 503 -> CreateListResponseFailure401(response.body<Error>())
        410 -> CreateListResponseFailure
        else -> CreateListResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateListResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateListRequest(
    public val exclusive: Boolean? = null,
    @SerialName("replies_policy")
    public val repliesPolicy: ListRepliesPolicyEnum? = null,
    public val title: String,
  )

  @Serializable
  public sealed class CreateListResponse

  @Serializable
  public data class CreateListResponseSuccess(
    public val body: List,
  ) : CreateListResponse()

  @Serializable
  public data class CreateListResponseFailure401(
    public val body: Error,
  ) : CreateListResponse()

  @Serializable
  public object CreateListResponseFailure : CreateListResponse()

  @Serializable
  public data class CreateListResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateListResponse()
}
