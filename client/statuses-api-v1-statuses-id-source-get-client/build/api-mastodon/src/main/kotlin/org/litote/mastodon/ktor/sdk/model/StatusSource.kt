package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class StatusSource(
  public val id: String,
  @SerialName("spoiler_text")
  public val spoilerText: String,
  public val text: String,
)
