package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model

import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class QuoteApproval(
  public val automatic: List<QuoteApprovalAutomaticEnum>,
  @SerialName("current_user")
  public val currentUser: QuoteApprovalCurrentUserEnum = QuoteApprovalCurrentUserEnum.UNKNOWN_,
  public val manual: List<QuoteApprovalAutomaticEnum>,
)
