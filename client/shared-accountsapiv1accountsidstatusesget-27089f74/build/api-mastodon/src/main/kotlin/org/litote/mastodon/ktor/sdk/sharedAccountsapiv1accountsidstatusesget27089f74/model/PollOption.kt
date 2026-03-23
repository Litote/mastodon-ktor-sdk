package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget27089f74.model

import kotlin.Long
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PollOption(
  public val title: String,
  @SerialName("votes_count")
  public val votesCount: Long? = null,
)
