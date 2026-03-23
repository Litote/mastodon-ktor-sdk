package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model

import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RelationshipSeveranceEvent(
  @SerialName("created_at")
  public val createdAt: String,
  @SerialName("followers_count")
  public val followersCount: Long,
  @SerialName("following_count")
  public val followingCount: Long,
  public val id: String,
  public val purged: Boolean,
  @SerialName("target_name")
  public val targetName: String,
  public val type: RelationshipSeveranceEventTypeEnum = RelationshipSeveranceEventTypeEnum.UNKNOWN_,
)
