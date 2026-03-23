package org.litote.mastodon.ktor.sdk.model

import kotlin.Boolean
import kotlin.String
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum

public interface BaseStatus {
  public val inReplyToId: String?

  public val language: String?

  public val quoteApprovalPolicy: String?

  public val quotedStatusId: String?

  public val scheduledAt: String?

  public val sensitive: Boolean?

  public val spoilerText: String?

  public val visibility: StatusVisibilityEnum?
}
