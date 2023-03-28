pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion apply false
        //id ("com.android.application")  version '7.4.2' apply false
        //id ("com.android.library")  version '7.4.2' apply false
        //id ("org.jetbrains.kotlin.android") // version '1.8.10' apply false
        id ("com.google.gms.google-services")  version "4.3.13" apply false
        id ("com.google.firebase.crashlytics")  version "2.9.1" apply false
        id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")  version "2.0.1" apply false

    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Bero"
include(":app")


