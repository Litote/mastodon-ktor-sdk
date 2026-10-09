plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // api: Status, Notification and Search types are part of ReadSdk's public API.
            api(project(":sdk:api"))
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
