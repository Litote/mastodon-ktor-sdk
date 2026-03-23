package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class FilterStatus(
  public val id: String,
  @SerialName("status_id")
  public val statusId: String,
)
