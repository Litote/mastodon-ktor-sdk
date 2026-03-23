package org.litote.mastodon.ktor.sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AsyncRefreshResponse(
  @SerialName("async_refresh")
  public val asyncRefresh: AsyncRefresh,
)
