// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("room_version", "2.6.0")
    }
}


plugins {
    val room_version = "2.8.4"
    id("androidx.room") version "$room_version" apply false
//    alias(libs.plugins.androidx.room)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
//    alias(libs.plugins.ksp)

//    id("com.google.devtools.ksp") version "2.3.5" apply false
//    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false

}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}