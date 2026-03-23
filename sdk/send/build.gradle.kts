plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":client:statuses-api-v1-statuses-post-client"))
            implementation(project(":client:media-api-v2-media-post-client"))
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
        description = "Mastodon SendSDK"
    }
}
