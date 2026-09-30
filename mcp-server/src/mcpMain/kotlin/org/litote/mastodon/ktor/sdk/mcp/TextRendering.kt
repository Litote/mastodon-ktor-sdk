package org.litote.mastodon.ktor.sdk.mcp

import org.litote.mastodon.ktor.sdk.model.Search
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsgetE402785c.model.Notification

private val htmlLineBreak = Regex("(?i)<br\\s*/?>|</p>\\s*<p[^>]*>")
private val htmlTag = Regex("<[^>]+>")

/** Converts Mastodon status HTML into simplified plain text: paragraphs and `<br>` become new lines, tags are dropped. */
internal fun htmlToText(html: String): String =
    html
        .replace(htmlLineBreak, "\n")
        .replace(htmlTag, "")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .trim()

/** Renders a status (or the boosted status for a boost) as a compact multi-line text block. */
internal fun Status.toText(): String {
    val shown = reblog ?: this
    val boostedBy = if (reblog != null) " (boosted by @${account.acct})" else ""
    val contentWarning = shown.spoilerText.takeIf { it.isNotBlank() }?.let { "[CW: $it] " } ?: ""
    return "@${shown.account.acct} · ${shown.createdAt}$boostedBy\n" +
        "$contentWarning${htmlToText(shown.content)}\n" +
        "${shown.url ?: shown.uri} (id: ${shown.id})"
}

/** Renders a notification as its type and author, followed by the related status if any. */
internal fun Notification.toText(): String {
    val header = "${type.serialName()} from @${account.acct} · $createdAt"
    return status?.let { "$header\n${htmlToText(it.content)}\n${it.url ?: it.uri} (id: ${it.id})" } ?: header
}

/** Renders search results grouped by kind, skipping empty groups. */
internal fun Search.toText(): String {
    val sections =
        listOfNotNull(
            accounts
                .takeIf { it.isNotEmpty() }
                ?.joinToString("\n", prefix = "Accounts:\n") { "- @${it.acct} (${it.displayName}) ${it.url ?: it.uri}" },
            statuses
                .takeIf { it.isNotEmpty() }
                ?.joinToString("\n\n", prefix = "Statuses:\n") { it.toText() },
            hashtags
                .takeIf { it.isNotEmpty() }
                ?.joinToString("\n", prefix = "Hashtags:\n") { "- #${it.name} ${it.url}" },
        )
    return sections.joinToString("\n\n").ifEmpty { "No results." }
}
