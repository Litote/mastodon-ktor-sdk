package org.litote.mastodon.ktor.sdk.sample

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.litote.mastodon.ktor.sdk.api.model.TextStatus
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration
import org.litote.mastodon.ktor.sdk.sample.client.AccountsClient
import org.litote.mastodon.ktor.sdk.sample.client.AccountsClient.GetAccountResponseSuccess
import org.litote.mastodon.ktor.sdk.sample.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.send.SendResult
import org.litote.mastodon.ktor.sdk.send.SendSdk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.litote.mastodon.ktor.sdk.api.model.Account as SdkAccount
import org.litote.mastodon.ktor.sdk.sample.model.Account as CustomAccount

class CustomClientTest {
    private val accountJson =
        """
        {
          "id": "1",
          "acct": "test",
          "username": "test",
          "avatar": "https://example.com/avatar.jpg",
          "avatar_static": "https://example.com/avatar.jpg",
          "bot": false,
          "created_at": "2024-01-01T00:00:00Z",
          "display_name": "Test User",
          "emojis": [],
          "fields": [],
          "followers_count": 0,
          "following_count": 0,
          "group": false,
          "header": "https://example.com/header.jpg",
          "header_static": "https://example.com/header.jpg",
          "locked": false,
          "note": "Test bio",
          "statuses_count": 0,
          "uri": "https://mastodon.social/users/test"
        }
        """.trimIndent()

    private val jsonConfig =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    private val requests = mutableListOf<HttpRequestData>()

    private fun accountsClient(): AccountsClient {
        val client =
            HttpClient(
                MockEngine { request ->
                    requests += request
                    respond(accountJson, headers = headersOf(HttpHeaders.ContentType, "application/json"))
                },
            ) {
                install(ContentNegotiation) { json(jsonConfig) }
                defaultRequest { url("https://mastodon.social/") }
            }
        return AccountsClient(ClientConfiguration(baseUrl = "https://mastodon.social/", client = client, json = jsonConfig))
    }

    @Test
    fun `GIVEN a client generated with a path filter WHEN getAccount THEN returns the account`() =
        runTest {
            val response = accountsClient().getAccount("1")

            val account = assertIs<GetAccountResponseSuccess>(response).body
            assertEquals("test", account.username)
            assertEquals("/api/v1/accounts/1", requests.single().url.encodedPath)
        }

    @Test
    fun `GIVEN a custom client and SendSdk on the classpath WHEN both are used THEN their models do not collide`() =
        runTest {
            val sendResult =
                SendSdk(SdkConfiguration(server = "mastodon.social", token = "token", simulate = true))
                    .sendText(TextStatus(status = "Hello"))
            val response = accountsClient().getAccount("1")

            assertIs<SendResult.Simulated>(sendResult)
            assertIs<CustomAccount>(assertIs<GetAccountResponseSuccess>(response).body)
            assertEquals("org.litote.mastodon.ktor.sdk.sample.model.Account", CustomAccount::class.qualifiedName)
            assertEquals("org.litote.mastodon.ktor.sdk.api.model.Account", SdkAccount::class.qualifiedName)
        }
}
