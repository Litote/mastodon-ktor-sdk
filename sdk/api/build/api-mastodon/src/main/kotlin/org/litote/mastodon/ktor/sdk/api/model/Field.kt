package org.litote.mastodon.ktor.sdk.api.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Field(
  public val name: String,
  public val `value`: String,
  @SerialName("verified_at")
  public val verifiedAt: String? = null,
)
