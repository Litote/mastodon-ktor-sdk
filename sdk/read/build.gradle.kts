plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // api: Status, Notification and Search types are part of ReadSdk's public API.
            api(project(":client:timelines-api-v1-timelines-home-get-client"))
            api(project(":client:notifications-api-v1-notifications-get-client"))
            api(project(":client:search-api-v2-search-get-client"))
            implementation(project(":sdk:configuration"))
        }

        jvmTest.dependencies {
            implementation(libs.ktor.client.mock)
            implementation(libs.coroutines.test)
        }
    }
}

mavenPublishing {
    pom {
        description = "Mastodon ReadSDK"
    }
}
