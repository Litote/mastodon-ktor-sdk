package org.litote.mastodon.ktor.sdk.send

import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client.StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponse
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client.StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseFailure
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client.StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseFailure401
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client.StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseFailure410
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client.StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseSuccess
import org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client.StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseUnknownFailure

/**
 * Sealed result type returned by [SendSdk.deleteStatus].
 */
public sealed class DeleteResult {
    /**
     * The status was deleted.
     *
     * @property status The deleted status, as returned by the server.
     */
    public data class Success(
        val status: Status,
    ) : DeleteResult()

    /**
     * The server refused to delete the status.
     *
     * @property id ID of the status that could not be deleted.
     * @property response The raw API response received from the statuses endpoint.
     */
    public data class Failure(
        val id: String,
        val response: DeleteStatusResponse,
    ) : DeleteResult() {
        /** Human-readable description of the failure, including the server error message when available. */
        public val errorMessage: String
            get() =
                "Failed to delete status $id: " +
                    when (response) {
                        is DeleteStatusResponseFailure401 -> response.body.describe()
                        is DeleteStatusResponseFailure -> response.body.error
                        is DeleteStatusResponseFailure410 -> "HTTP 410"
                        is DeleteStatusResponseUnknownFailure -> "HTTP ${response.statusCode}"
                        is DeleteStatusResponseSuccess -> "unexpected success response"
                    }
    }

    /**
     * The status was not deleted because simulate mode is active.
     *
     * @property id ID of the status that would have been deleted.
     */
    public data class Simulated(
        val id: String,
    ) : DeleteResult()
}
