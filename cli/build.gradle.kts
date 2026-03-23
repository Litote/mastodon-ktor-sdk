plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":sdk:send"))
            implementation(project(":sdk:configuration"))
            implementation(project(":client:statuses-api-v1-statuses-post-client"))
            implementation(project(":client:media-api-v2-media-post-client"))
            implementation(project(":client:shared-accountsapiv1accountsidstatusesget-4016b7e9"))
            implementation(libs.kotlinx.io.core)
        }
        jvmMain.dependencies {
            runtimeOnly(libs.slf4j.simple)
        }
    }
}

mavenPublishing {
    pom {
        description = "Mastodon CLI"
    }
}
