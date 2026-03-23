package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model

import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter

@Serializable
public data class FilterResult(
  public val filter: Filter,
  @SerialName("keyword_matches")
  public val keywordMatches: List<String>? = null,
  @SerialName("status_matches")
  public val statusMatches: List<String>? = null,
)
