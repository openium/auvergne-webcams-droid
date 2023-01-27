# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/nitrog42/Dev/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class fr.openium.auvergnewebcams.model.** { *; }
-keep class fr.openium.auvergnewebcams.rest.** { *; }

-keepattributes Signature

# Kotlin
-keep class kotlin.coroutines.Continuation

# RxJava
-keep class io.reactivex.Observable { *; }
-keep class * extends io.reactivex.Observable
-keep class io.reactivex.Single { *; }
-keep class * extends io.reactivex.Single
-keep class io.reactivex.Maybe { *; }
-keep class * extends io.reactivex.Maybe
-keep class io.reactivex.Flowable { *; }
-keep class * extends io.reactivex.Flowable

# Retrofit
-keep class retrofit2.Response { *; }
-keep class * extends retrofit2.Response
-keep class retrofit2.adapter.rxjava2.Result { *; }
-keep class * extends retrofit2.adapter.rxjava2.Result
-keep class retrofit2.Converter { *; }
-keep class * extends retrofit2.Converter

# GSON
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken