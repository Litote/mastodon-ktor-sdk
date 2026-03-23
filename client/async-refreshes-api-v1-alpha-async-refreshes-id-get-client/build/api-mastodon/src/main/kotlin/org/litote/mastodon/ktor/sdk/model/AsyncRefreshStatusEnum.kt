package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
public enum class AsyncRefreshStatusEnum {
  @SerialName("running")
  RUNNING,
  @SerialName("finished")
  FINISHED,
  UNKNOWN_,
  ;

  public fun serialName(): String = AsyncRefreshStatusEnum.serializer().descriptor.getElementName(this.ordinal)
}
