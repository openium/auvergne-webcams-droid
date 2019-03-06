// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra["kotlinVersion"] = "1.3.21"

    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
        maven("http://maven.openium.fr")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-alpha06")
        classpath(kotlin("gradle-plugin", version = extra["kotlinVersion"].toString()))
        classpath("com.google.gms:google-services:4.2.0")
        classpath("io.realm:realm-gradle-plugin:5.9.0")
        classpath("fr.openium:version-plugin:1.2")
        classpath("io.fabric.tools:gradle:1.27.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://www.jitpack.io")
        maven("http://dl.bintray.com/piasy/maven")
        maven("https://maven.google.com")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}