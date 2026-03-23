package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class ListRepliesPolicyEnum {
  @SerialName("followed")
  FOLLOWED,
  @SerialName("list")
  LIST,
  @SerialName("none")
  NONE,
  UNKNOWN_,
  ;

  public fun serialName(): String = ListRepliesPolicyEnum.serializer().descriptor.getElementName(this.ordinal)
}
