package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model

import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum

@Serializable
public data class Filter(
  public val context: List<FilterContextEnum>,
  @SerialName("expires_at")
  public val expiresAt: String? = null,
  @SerialName("filter_action")
  public val filterAction: FilterFilterActionEnum = FilterFilterActionEnum.UNKNOWN_,
  public val id: String,
  public val keywords: List<FilterKeyword>? = null,
  public val statuses: List<FilterStatus>? = null,
  public val title: String,
)
