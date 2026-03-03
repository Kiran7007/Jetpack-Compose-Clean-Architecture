plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Core module
    implementation(project(":core"))

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")

    // Paging
    api("androidx.paging:paging-common:${rootProject.extra["paging_version"]}")

    // Testing
    testImplementation("junit:junit:${rootProject.extra["junit_version"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockk_version"]}")
}
