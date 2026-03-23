package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget10fddec7.model

import kotlin.Boolean
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CustomEmoji(
  public val category: String? = null,
  public val shortcode: String,
  @SerialName("static_url")
  public val staticUrl: String,
  public val url: String,
  @SerialName("visible_in_picker")
  public val visibleInPicker: Boolean,
)
