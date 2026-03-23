package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class AppealStateEnum {
  @SerialName("approved")
  APPROVED,
  @SerialName("rejected")
  REJECTED,
  @SerialName("pending")
  PENDING,
  UNKNOWN_,
  ;

  public fun serialName(): String = AppealStateEnum.serializer().descriptor.getElementName(this.ordinal)
}
