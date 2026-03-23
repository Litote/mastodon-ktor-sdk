package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget2b22751c.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class TrendsLinkTypeEnum {
  @SerialName("link")
  LINK,
  @SerialName("photo")
  PHOTO,
  @SerialName("video")
  VIDEO,
  @SerialName("rich")
  RICH,
  UNKNOWN_,
  ;

  public fun serialName(): String = TrendsLinkTypeEnum.serializer().descriptor.getElementName(this.ordinal)
}
