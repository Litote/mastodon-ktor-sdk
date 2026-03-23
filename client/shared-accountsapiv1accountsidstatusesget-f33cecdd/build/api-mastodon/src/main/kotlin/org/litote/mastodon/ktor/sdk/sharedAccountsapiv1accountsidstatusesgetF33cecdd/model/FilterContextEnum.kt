package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class FilterContextEnum {
  @SerialName("home")
  HOME,
  @SerialName("notifications")
  NOTIFICATIONS,
  @SerialName("public")
  PUBLIC,
  @SerialName("thread")
  THREAD,
  @SerialName("account")
  ACCOUNT,
  UNKNOWN_,
  ;

  public fun serialName(): String = FilterContextEnum.serializer().descriptor.getElementName(this.ordinal)
}
