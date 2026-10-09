package org.litote.mastodon.ktor.sdk.read

import org.litote.mastodon.ktor.sdk.api.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.api.client.NotificationsClient
import org.litote.mastodon.ktor.sdk.api.client.NotificationsClient.GetNotificationsResponseFailure
import org.litote.mastodon.ktor.sdk.api.client.NotificationsClient.GetNotificationsResponseFailure401
import org.litote.mastodon.ktor.sdk.api.client.NotificationsClient.GetNotificationsResponseFailure410
import org.litote.mastodon.ktor.sdk.api.client.NotificationsClient.GetNotificationsResponseSuccess
import org.litote.mastodon.ktor.sdk.api.client.NotificationsClient.GetNotificationsResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.api.client.SearchClient
import org.litote.mastodon.ktor.sdk.api.client.SearchClient.GetSearchV2ResponseFailure
import org.litote.mastodon.ktor.sdk.api.client.SearchClient.GetSearchV2ResponseFailure401
import org.litote.mastodon.ktor.sdk.api.client.SearchClient.GetSearchV2ResponseFailure410
import org.litote.mastodon.ktor.sdk.api.client.SearchClient.GetSearchV2ResponseSuccess
import org.litote.mastodon.ktor.sdk.api.client.SearchClient.GetSearchV2ResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient.GetTimelineHomeResponseFailure
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient.GetTimelineHomeResponseFailure401
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient.GetTimelineHomeResponseFailure410
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient.GetTimelineHomeResponseSuccess
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient.GetTimelineHomeResponseSuccess200
import org.litote.mastodon.ktor.sdk.api.client.TimelinesClient.GetTimelineHomeResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.api.model.Error
import org.litote.mastodon.ktor.sdk.api.model.Notification
import org.litote.mastodon.ktor.sdk.api.model.Search
import org.litote.mastodon.ktor.sdk.api.model.Status
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.configuration.toClientConfiguration

/** Maximum number of items that can be requested in a single read call. */
public const val MAX_READ_LIMIT: Int = 40

/** Number of items returned when no limit is given. */
public const val DEFAULT_READ_LIMIT: Int = 20

private const val HTTP_GONE = "HTTP 410"

/** Kind of results a [ReadSdk.search] is restricted to. */
public enum class SearchType(
    internal val apiValue: String,
) {
    ACCOUNTS("accounts"),
    STATUSES("statuses"),
    HASHTAGS("hashtags"),
}

/**
 * Sealed result type returned by [ReadSdk] operations.
 */
public sealed class ReadResult<out T> {
    /**
     * The read request succeeded.
     *
     * @property value The data returned by the server.
     */
    public data class Success<out T>(
        val value: T,
    ) : ReadResult<T>()

    /**
     * The read request failed.
     *
     * @property errorMessage Human-readable description of the failure, including the server error message when available.
     */
    public data class Failure(
        val errorMessage: String,
    ) : ReadResult<Nothing>()
}

/**
 * High-level SDK for reading data from a Mastodon instance: home timeline, notifications and search.
 *
 * Read operations always contact the server, even when [SdkConfiguration.simulate] is `true`,
 * since they have no side effect.
 *
 * ```kotlin
 * val sdk = ReadSdk(SdkConfiguration(server = "mastodon.social", token = "…"))
 * when (val result = sdk.homeTimeline(limit = 10)) {
 *     is ReadResult.Success -> result.value.forEach { println(it.content) }
 *     is ReadResult.Failure -> println(result.errorMessage)
 * }
 * ```
 *
 * @param clientConfig Low-level configuration used by the generated API clients.
 */
public class ReadSdk public constructor(
    private val clientConfig: ClientConfiguration,
) {
    /** Creates a [ReadSdk] configured from the given [SdkConfiguration]. */
    public constructor(config: SdkConfiguration) : this(config.toClientConfiguration())

    /**
     * Returns the most recent statuses of the authenticated user's home timeline.
     *
     * @param limit Number of statuses to return, between 1 and [MAX_READ_LIMIT].
     * @throws IllegalArgumentException if [limit] is out of range.
     */
    public suspend fun homeTimeline(limit: Int = DEFAULT_READ_LIMIT): ReadResult<List<Status>> {
        requireLimit(limit)
        val prefix = "Failed to read home timeline"
        return when (val response = TimelinesClient(clientConfig).getTimelineHome(limit = limit.toLong())) {
            is GetTimelineHomeResponseSuccess200 -> ReadResult.Success(response.body)
            is GetTimelineHomeResponseSuccess -> failure(prefix, "timeline is being regenerated, try again later")
            is GetTimelineHomeResponseFailure401 -> failure(prefix, response.body.describe())
            is GetTimelineHomeResponseFailure -> failure(prefix, response.body.error)
            is GetTimelineHomeResponseFailure410 -> failure(prefix, HTTP_GONE)
            is GetTimelineHomeResponseUnknownFailure -> failure(prefix, "HTTP ${response.statusCode}")
        }
    }

    /**
     * Returns the most recent notifications of the authenticated user.
     *
     * @param limit Number of notifications to return, between 1 and [MAX_READ_LIMIT].
     * @throws IllegalArgumentException if [limit] is out of range.
     */
    public suspend fun notifications(limit: Int = DEFAULT_READ_LIMIT): ReadResult<List<Notification>> {
        requireLimit(limit)
        val prefix = "Failed to read notifications"
        return when (val response = NotificationsClient(clientConfig).getNotifications(limit = limit.toLong())) {
            is GetNotificationsResponseSuccess -> ReadResult.Success(response.body)
            is GetNotificationsResponseFailure401 -> failure(prefix, response.body.describe())
            is GetNotificationsResponseFailure -> failure(prefix, response.body.error)
            is GetNotificationsResponseFailure410 -> failure(prefix, HTTP_GONE)
            is GetNotificationsResponseUnknownFailure -> failure(prefix, "HTTP ${response.statusCode}")
        }
    }

    /**
     * Searches accounts, statuses and hashtags.
     *
     * @param query Text to search for. Must not be blank.
     * @param type Restricts results to a single kind, or `null` for all kinds.
     * @param limit Maximum number of results per kind, between 1 and [MAX_READ_LIMIT].
     * @throws IllegalArgumentException if [query] is blank or [limit] is out of range.
     */
    public suspend fun search(
        query: String,
        type: SearchType? = null,
        limit: Int = DEFAULT_READ_LIMIT,
    ): ReadResult<Search> {
        require(query.isNotBlank()) { "Search query is required" }
        requireLimit(limit)
        val prefix = "Failed to search"
        val response =
            SearchClient(clientConfig).getSearchV2(
                q = query,
                limit = limit.toLong(),
                type = type?.apiValue,
            )
        return when (response) {
            is GetSearchV2ResponseSuccess -> ReadResult.Success(response.body)
            is GetSearchV2ResponseFailure401 -> failure(prefix, response.body.describe())
            is GetSearchV2ResponseFailure -> failure(prefix, response.body.error)
            is GetSearchV2ResponseFailure410 -> failure(prefix, HTTP_GONE)
            is GetSearchV2ResponseUnknownFailure -> failure(prefix, "HTTP ${response.statusCode}")
        }
    }
}

private fun requireLimit(limit: Int) {
    require(limit in 1..MAX_READ_LIMIT) { "Limit must be between 1 and $MAX_READ_LIMIT" }
}

private fun failure(
    prefix: String,
    detail: String,
): ReadResult.Failure = ReadResult.Failure("$prefix: $detail")

private fun Error.describe(): String = errorDescription?.let { "$error ($it)" } ?: error
