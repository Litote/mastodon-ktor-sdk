package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model

import kotlin.Boolean
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class List(
  public val exclusive: Boolean,
  public val id: String,
  @SerialName("replies_policy")
  public val repliesPolicy: ListRepliesPolicyEnum = ListRepliesPolicyEnum.UNKNOWN_,
  public val title: String,
)
