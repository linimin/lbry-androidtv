/*
 * MIT License
 *
 * Copyright (c) 2022 LIN I MIN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

// [Configure your build](https://developer.android.com/studio/build)

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safeArgs)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.googleServices)
    // https://github.com/firebase/firebase-android-sdk/issues/2665#issuecomment-849897741
    alias(libs.plugins.firebaseCrashlytics)
}

// https://developer.android.com/studio/build/configure-app-module
android {
    // https://developer.android.com/studio/build/configure-app-module#set-namespace
    namespace = "app.newproj.lbrytv"
    compileSdk = 31
    defaultConfig {
        applicationId = "app.newproj.lbrytv"
        minSdk = 26
        targetSdk = 31
        versionCode = 6
        versionName = "1.0.0-beta.4"
        javaCompileOptions {
            annotationProcessorOptions {
                /*
                 * [Configuring compiler options for Room](https://developer.android.com/jetpack/androidx/releases/room#compiler-options)
                 */
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true",
                    )
                )
            }
        }
        // https://developer.android.com/training/testing/instrumented-tests#set-testing
        testInstrumentationRunner = "app.newproj.lbrytv.HiltTestRunner"
    }
    flavorDimensions += "device"

    productFlavors {
        create("tv")
    }

    signingConfigs {
        register("release") {
            rootProject.file("keystore.properties")
                .takeIf { it.exists() }
                ?.let {
                    val keystoreProperties = Properties()
                    keystoreProperties.load(FileInputStream(it))
                    keyAlias = keystoreProperties["keyAlias"] as String
                    keyPassword = keystoreProperties["keyPassword"] as String
                    storeFile = file(keystoreProperties["storeFile"] as String)
                    storePassword = keystoreProperties["storePassword"] as String
                }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // [Use Java 8 language features and APIs](https://developer.android.com/studio/write/java8-support)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions.jvmTarget = "1.8"

    buildFeatures {
        viewBinding = true // https://developer.android.com/topic/libraries/view-binding
        dataBinding = true // https://developer.android.com/topic/libraries/data-binding
    }

    testOptions {
        unitTests.all {
            // https://docs.gradle.org/current/userguide/java_testing.html#using_junit5
            // https://www.lordcodes.com/articles/testing-on-android-using-junit-5
            it.useJUnitPlatform()
        }
        unitTests.isIncludeAndroidResources = true
    }

    packagingOptions {
        resources {
            // JUnit 5 will bundle in files with identical paths, exclude them.
            excludes.add("META-INF/LICENSE*")
        }
    }
}

// [Using kapt](https://kotlinlang.org/docs/kapt.html)
kapt {
    // Required by Hilt: https://dagger.dev/hilt/gradle-setup.html
    correctErrorTypes = true
}

// [Opt-in requirements](https://kotlinlang.org/docs/opt-in-requirements.html)
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
            }
        }
    }
}

// [Gradle dependency configurations](https://docs.gradle.org/current/userguide/declaring_dependencies.html)
dependencies {
    implementation(libs.bitcoinj)
    implementation(libs.blitz)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.leanback)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.media3)
    implementation(libs.bundles.room)
    implementation(libs.coil)
    implementation(libs.constraintLayout)
    implementation(libs.core)
    implementation(libs.dataStore)
    implementation(libs.fragment)
    implementation(libs.gson)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.paging)
    implementation(libs.protobuf.javaLite)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.splashScreen)
    implementation(libs.startup)
    implementation(libs.swipeRefreshLayout)
    implementation(libs.timber)
    implementation(libs.tvProvider)
    implementation(libs.workerManager)
    implementation(libs.zxing)
    implementation(platform(libs.firebaseBom))

    kapt(libs.hilt.compiler)
    kapt(libs.room.compiler)
    kaptAndroidTest(libs.hilt.compiler)
    kaptTest(libs.hilt.compiler)

    androidTestImplementation(libs.bundles.test.instrumented)
    testImplementation(libs.bundles.test.local)
    testRuntimeOnly(libs.bundles.test.local.runtime)
}
