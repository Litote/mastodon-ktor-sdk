package org.litote.mastodon.ktor.sdk.model

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget10fddec7.model.CustomEmoji
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetDdf78166.model.MediaAttachment

@Serializable
public data class StatusEdit(
  public val account: Account,
  public val content: String,
  @SerialName("created_at")
  public val createdAt: String,
  public val emojis: List<CustomEmoji>,
  @SerialName("media_attachments")
  public val mediaAttachments: List<MediaAttachment>,
  public val poll: Poll? = null,
  public val quote: JsonElement? = null,
  public val sensitive: Boolean,
  @SerialName("spoiler_text")
  public val spoilerText: String,
) {
  @Serializable
  public data class Poll(
    public val options: List<JsonElement>,
  )
}
