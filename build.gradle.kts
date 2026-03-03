// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("kotlin_version", "1.9.22")
        set("compose_version", "1.6.0")
        set("compose_compiler_version", "1.5.8")
        set("hilt_version", "2.50")
        set("room_version", "2.6.1")
        set("retrofit_version", "2.9.0")
        set("okhttp_version", "4.12.0")
        set("paging_version", "3.2.1")
        set("navigation_version", "2.7.6")
        set("work_version", "2.9.0")
        set("coroutines_version", "1.7.3")
        set("lifecycle_version", "2.7.0")
        set("junit_version", "4.13.2")
        set("mockk_version", "1.13.9")
        set("turbine_version", "1.0.0")
        set("firebase_bom_version", "32.7.1")
    }
}

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("com.android.library") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
