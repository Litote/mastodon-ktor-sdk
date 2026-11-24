plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // TextStatus, MediaStatus, CreateMediaV2Form and response types are part of SendSdk's public API.
            api(project(":sdk:api"))
            implementation(project(":sdk:configuration"))
        }

        jvmTest.dependencies {
         implementation(openapi.ktor.client.mock)
            implementation(openapi.coroutines.test)
        }
    }
}

mavenPublishing {
    pom {
        description = "Mastodon SendSDK"
    }
}
