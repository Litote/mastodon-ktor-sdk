package org.litote.mastodon.ktor.sdk.sharedInstanceapiv1instanceget2050c4cc.model

import kotlin.String
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class Rule(
  public val hint: String? = null,
  public val id: String,
  public val text: String,
  public val translations: JsonElement? = null,
)
