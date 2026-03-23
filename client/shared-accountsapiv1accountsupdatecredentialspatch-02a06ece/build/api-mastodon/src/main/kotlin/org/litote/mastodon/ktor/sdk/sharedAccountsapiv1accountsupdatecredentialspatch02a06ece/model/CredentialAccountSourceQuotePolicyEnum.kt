package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsupdatecredentialspatch02a06ece.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class CredentialAccountSourceQuotePolicyEnum {
  @SerialName("public")
  PUBLIC,
  @SerialName("followers")
  FOLLOWERS,
  @SerialName("nobody")
  NOBODY,
  UNKNOWN_,
  ;

  public fun serialName(): String = CredentialAccountSourceQuotePolicyEnum.serializer().descriptor.getElementName(this.ordinal)
}
