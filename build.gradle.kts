// Top-level build file where you can add configuration options common to all sub-projects/modules.

// The Hilt Gradle plugin has no plugin-marker artifact on the Gradle Plugin Portal
// or Maven Central, so it must be added to the buildscript classpath directly.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}

plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.23" apply false
}