package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget35be95f4.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class ReportCategoryEnum {
  @SerialName("spam")
  SPAM,
  @SerialName("legal")
  LEGAL,
  @SerialName("violation")
  VIOLATION,
  @SerialName("other")
  OTHER,
  UNKNOWN_,
  ;

  public fun serialName(): String = ReportCategoryEnum.serializer().descriptor.getElementName(this.ordinal)
}
