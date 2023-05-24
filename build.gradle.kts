import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()

    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.android.tools.build:gradle:8.0.1")
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven ( url = "https://oss.sonatype.org/content/repositories/snapshots/" )
        maven (url = "https://jitpack.io" )
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}


