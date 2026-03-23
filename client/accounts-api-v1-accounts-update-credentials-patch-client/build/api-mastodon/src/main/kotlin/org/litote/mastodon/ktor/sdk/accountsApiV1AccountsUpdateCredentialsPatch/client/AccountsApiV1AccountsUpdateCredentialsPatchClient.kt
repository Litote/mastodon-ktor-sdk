package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsUpdateCredentialsPatch.client

import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsupdatecredentialspatch02a06ece.model.CredentialAccount

public class AccountsApiV1AccountsUpdateCredentialsPatchClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Update account credentials
   */
  public suspend fun patchAccountsUpdateCredentials(request: PatchAccountsUpdateCredentialsRequest): PatchAccountsUpdateCredentialsResponse {
    try {
      val response = configuration.client.patch("api/v1/accounts/update_credentials") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PatchAccountsUpdateCredentialsResponseSuccess(response.body<CredentialAccount>())
        401, 404, 422, 429, 503 -> PatchAccountsUpdateCredentialsResponseFailure401(response.body<Error>())
        410 -> PatchAccountsUpdateCredentialsResponseFailure
        else -> PatchAccountsUpdateCredentialsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PatchAccountsUpdateCredentialsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PatchAccountsUpdateCredentialsRequest(
    @SerialName("attribution_domains")
    public val attributionDomains: List<String>? = null,
    public val avatar: String? = null,
    public val bot: Boolean? = null,
    public val discoverable: Boolean? = null,
    @SerialName("display_name")
    public val displayName: String? = null,
    @SerialName("fields_attributes")
    public val fieldsAttributes: JsonElement? = null,
    public val `header`: String? = null,
    @SerialName("hide_collections")
    public val hideCollections: Boolean? = null,
    public val indexable: Boolean? = null,
    public val locked: Boolean? = null,
    public val note: String? = null,
    public val source: Source? = null,
  ) {
    @Serializable
    public data class Source(
      public val language: String? = null,
      public val privacy: StatusVisibilityEnum? = null,
      @SerialName("quote_policy")
      public val quotePolicy: String? = null,
      public val sensitive: Boolean? = null,
    )
  }

  @Serializable
  public sealed class PatchAccountsUpdateCredentialsResponse

  @Serializable
  public data class PatchAccountsUpdateCredentialsResponseSuccess(
    public val body: CredentialAccount,
  ) : PatchAccountsUpdateCredentialsResponse()

  @Serializable
  public data class PatchAccountsUpdateCredentialsResponseFailure401(
    public val body: Error,
  ) : PatchAccountsUpdateCredentialsResponse()

  @Serializable
  public object PatchAccountsUpdateCredentialsResponseFailure : PatchAccountsUpdateCredentialsResponse()

  @Serializable
  public data class PatchAccountsUpdateCredentialsResponseUnknownFailure(
    public val statusCode: Int,
  ) : PatchAccountsUpdateCredentialsResponse()
}
