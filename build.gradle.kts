// Top-level build file where you can add configuration options common to all sub-projects/modules.
//buildscript {
//    repositories {
//        maven {
//            url = uri("https://artifactory.2gis.dev/sdk-maven-release")
//        }
//    }
//}
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("androidx.navigation.safeargs") version "2.5.3" apply false
}