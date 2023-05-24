import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id ("org.jetbrains.kotlin.kapt")
    //kotlin("multiplatform")
    //id("com.google.devtools.ksp")
}
/*
kotlin {
    jvm {
        withJava()
    }
    linuxX64() {
        binaries {
            executable()
        }
    }
    sourceSets {
        val commonMain by getting
        val linuxX64Main by getting
        val linuxX64Test by getting
    }
}
*/
kotlin {
    jvmToolchain(17)
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
@Suppress("UnstableApiUsage")
android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.ironraft.pupping.bero"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("releaseWithSignedKey") {
            storeFile = file("bero_release_key")
            storePassword = "12qw34ER"
            keyAlias = "bero"
            keyPassword = "12qw34ER"
        }
    }
    kapt {
        correctErrorTypes = true
    }
    //koin
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }

    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("releaseWithSignedKey")
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("demo") {
            dimension = "version"
            buildConfigField("String", "APP_REST_ADDRESS", "\"http://acf14fe3f33d448ed863b0965826ef23-882930440.ap-northeast-2.elb.amazonaws.com/\"")
        }
        create("full"){
            dimension = "version"
            buildConfigField("String", "APP_REST_ADDRESS", "\"http://acf14fe3f33d448ed863b0965826ef23-882930440.ap-northeast-2.elb.amazonaws.com/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    /*
    withGroovyBuilder {
        "kotlinOptions" {
            setProperty("jvmTarget", "1.8")
        }
    }*/
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    namespace = "com.ironraft.pupping.bero"
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    /**
     * koin
     */
    implementation("io.insert-koin:koin-core:3.4.0")
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-android-compat:3.4.0")
    implementation("io.insert-koin:koin-androidx-workmanager:3.4.0")
    implementation("io.insert-koin:koin-androidx-navigation:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.3")
    implementation("io.insert-koin:koin-annotations:1.2.0")
    implementation("dev.burnoo:cokoin:0.3.2")
    /*
    implementation("io.insert-koin:koin-ksp-compiler:1.0.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.0-1.0.8")
    add("kspCommonMainMetadata", project(":test-processor"))
    add("kspJvm", project(":test-processor"))
    add("kspJvmTest", project(":test-processor")) // Not doing anything because there's no test source set for JVM
    // There is no processing for the Linux x64 main source set, because kspLinuxX64 isn't specified
    add("kspLinuxX64Test", project(":test-processor"))
    */
    /**
     * compose
     */
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.runtime:runtime:1.4.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.25.0")
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("com.google.accompanist:accompanist-webview:0.24.13-rc")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.4.3")

    /**
     * system
     */
    implementation("com.jaredrummler:android-device-names:2.1.1")
    /**
     * ui
     */
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("com.ms-square:expandableTextView:0.1.4")

    /**
     * co-routines
     */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    /**
     * Firebase
     */
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-core")
    implementation("com.google.firebase:firebase-dynamic-links")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics")

    /**
     * Retrofit2
     */
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    /**
     * OkHttp3
     */
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")


    /**
     * Coil
     */
    implementation("io.coil-kt:coil-compose:2.3.0")

    /**
     * Exo Player
     */
    implementation("com.google.android.exoplayer:exoplayer:2.18.5")
    implementation("com.google.android.exoplayer:exoplayer-core:2.18.5")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.5")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.18.5")
    implementation("com.google.android.exoplayer:exoplayer-smoothstreaming:2.18.5")
    implementation("com.google.android.exoplayer:extension-cast:2.18.5")

    /**
     * REMOTE DEBUG LIB
     */
    implementation("com.facebook.stetho:stetho:1.6.0")
    implementation("com.facebook.stetho:stetho-js-rhino:1.6.0")
    /**
     * Memory leak
     */
    //debugimplementation("com.squareup.leakcanary:leakcanary-android:2.8.1"
    /**
     * Sns
     */
    implementation("com.facebook.android:facebook-login:16.0.0")
    /**
     * Firebase
     */
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    /**
     * Google Map
     */
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.libraries.places:places:3.1.0")
    implementation("com.google.maps.android:maps-compose:2.11.3")

    /**
     * Lib
     */
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    implementation("com.github.commandiron:WheelPickerCompose:1.1.10")
}