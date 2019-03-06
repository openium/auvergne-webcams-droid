import com.android.build.gradle.internal.dsl.SigningConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("realm-android")
    id("fr.openium.version")
    id("io.fabric")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "fr.openium.auvergnewebcams"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = openiumVersion.versionCode
        versionName = "1.4"

        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
        ndk {
            abiFilters("arm64-v8a", "armeabi-v7a", "x86")
        }
    }

    signingConfigs {
        add(SigningConfig("debug").apply {
            storeFile = file("keys/debug.keystore")
        })
        add(SigningConfig("release").apply {
            storeFile = file("keys/release.keystore")
            storePassword = "AWCOpenium"
            keyAlias = "AuvergneWebCamsOpenium"
            keyPassword = "AWCOpenium"
        })
    }

    buildTypes {
        getByName("debug") {
            extra["enableCrashlytics"] = false
            extra["alwaysUpdateBuildId"] = false

            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"

//            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            extra["enableCrashlytics"] = false

            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            print(signingConfigs.names)
            signingConfig = signingConfigs.getByName("release")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }
}

repositories {
    mavenCentral()
    google()
    jcenter()
    maven("https://jitpack.io")
    maven("https://maven.openium.fr")
    maven("https://maven.fabric.io/public")

    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // --- Kotlin ---
    implementation(kotlin("stdlib", "1.3.21"))

    // --- Android support ---
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.1.0-alpha04")
//    implementation 'androidx.multidex:multidex:2.0.1'

    // --- Firebase ---
    implementation("com.google.android.gms:play-services-maps:16.1.0")
    implementation("com.google.firebase:firebase-core:16.0.7")

    // --- Android Tools ---
    implementation("fr.openium:kotlin-tools:1.0.7")
    implementation("fr.openium:realm-tools:1.0.2")
    implementation("fr.openium:rx-tools:1.0.3")

    // --- KTX ---
    implementation("androidx.core:core-ktx:1.0.1")

    // --- Kodein ---
    implementation("org.kodein.di:kodein-di-generic-jvm:5.3.0")
    implementation("org.kodein.di:kodein-di-framework-android-x:5.3.0")

    // --- Rx ---
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.5")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("com.github.akarnokd:rxjava2-extensions:0.20.5")
    implementation("com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha2")
    implementation("com.jakewharton.rxbinding3:rxbinding-appcompat:3.0.0-alpha2")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.0")
    implementation("com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar")

    // --- Retrofit ---
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")
    implementation("com.google.code.gson:gson:2.8.5")
    debugImplementation("com.squareup.retrofit2:retrofit-mock:2.5.0")
    implementation("com.squareup.okio:okio:2.1.0")

    // --- Log ---
    implementation("com.jakewharton.timber:timber:4.7.1")

    // --- Crashlytics ---
    implementation("com.crashlytics.sdk.android:crashlytics:2.9.9")
    implementation("com.crashlytics.sdk.android:answers:1.4.7")

    // --- Test ---
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:core:1.1.0")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.1.1")
    androidTestUtil("androidx.test:orchestrator:1.1.1")

    // --- ARCH ---
    implementation("android.arch.lifecycle:extensions:1.1.1")
    implementation("android.arch.lifecycle:viewmodel:1.1.1")
    implementation("android.arch.lifecycle:livedata:1.1.1")
    kapt("android.arch.lifecycle:compiler:1.1.1")
    implementation("android.arch.lifecycle:reactivestreams:1.1.1")

    // --- Specific lib of the app ---

    //Glide
    implementation("com.github.bumptech.glide:glide:4.9.0")
    kapt("com.github.bumptech.glide:compiler:4.9.0")

    //CURL logger
    implementation("com.github.grapesnberries:curlloggerinterceptor:0.1")

    //Like button (Twitter)
    implementation("com.github.jd-alexander:LikeButton:0.2.3")

    //TurnLayoutManager
    implementation(group = "", ext = "aar", name = "turn-release")
}