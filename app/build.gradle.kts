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

// https://docs.gradle.org/current/dsl/

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    // https://kotlinlang.org/docs/ksp-overview.html
    alias(libs.plugins.kotlin.ksp)
    // https://kotlinlang.org/docs/kapt.html
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.jetpack.navigation.safeargs)
    alias(libs.plugins.protobuf)
    // https://developers.google.com/android/guides/google-services-plugin
    alias(libs.plugins.google.services)
    // https://github.com/firebase/firebase-android-sdk/issues/2665#issuecomment-849897741
    alias(libs.plugins.firebase.crashlytics)
}

// https://developer.android.com/studio/build/configure-app-module
// https://developer.android.com/studio/build/gradle-tips
android {
    // https://developer.android.com/studio/build/configure-app-module#set-namespace
    namespace = "app.newproj.lbrytv"
    compileSdk = libs.versions.buildOptions.compileSdk.get().toInt()

    defaultConfig {
        // https://developer.android.com/studio/build/configure-app-module#set-application-id
        applicationId = "app.newproj.lbrytv"
        minSdk = libs.versions.buildOptions.minSdk.get().toInt()
        targetSdk = libs.versions.buildOptions.targetSdk.get().toInt()
        versionCode = 7
        versionName = "1.0.1-beta.4"
        // https://developer.android.com/training/dependency-injection/hilt-testing#instrumented-tests
        testInstrumentationRunner = "app.newproj.lbrytv.HiltTestRunner"
        // https://developer.android.com/studio/build/shrink-code#unused-alt-resources
        resourceConfigurations += listOf("en", "zh-rTW")
    }

    // https://developer.android.com/studio/build/build-variants#signing
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

    // https://developer.android.com/studio/build/build-variants#build-types
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // https://developer.android.com/studio/build/shrink-code#enable
            isMinifyEnabled = true
            isShrinkResources = true
            // https://developer.android.com/studio/build/optimize-your-build#disable_crunching
            isCrunchPngs = false
            // https://developer.android.com/studio/build/shrink-code#android_gradle_plugin_version_41_or_later
            ndk.debugSymbolLevel = "SYMBOL_TABLE"
            // https://developer.android.com/studio/build/shrink-code#configuration-files
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "device"

    // https://developer.android.com/studio/build/build-variants#product-flavors
    productFlavors {
        create("tv")
    }

    // https://developer.android.com/studio/write/java8-support
    compileOptions {
        sourceCompatibility(libs.versions.java.get())
        targetCompatibility(libs.versions.java.get())
    }
    kotlinOptions.jvmTarget = libs.versions.java.get()

    buildFeatures {
        // https://developer.android.com/topic/libraries/view-binding
        viewBinding = true

        // https://developer.android.com/topic/libraries/data-binding
        dataBinding = true
    }

    // https://developer.android.com/studio/test/advanced-test-setup#configure-gradle-test-options
    testOptions {
        unitTests {
            all {
                // https://docs.gradle.org/current/userguide/java_testing.html#using_junit5
                // https://www.lordcodes.com/articles/testing-on-android-using-junit-5
                it.useJUnitPlatform()
            }
            isIncludeAndroidResources = true
        }
    }

    packagingOptions {
        resources {
            // JUnit 5 will bundle in files with identical paths, exclude them.
            excludes.add("META-INF/LICENSE*")
        }
    }
}

// [Opt-in requirements](https://kotlinlang.org/docs/opt-in-requirements.html)
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
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

kapt {
    // https://kotlinlang.org/docs/kapt.html#non-existent-type-correction
    // https://dagger.dev/hilt/gradle-setup
    correctErrorTypes = true
}

ksp {
    // https://developer.android.com/jetpack/androidx/releases/room#compiler-options

    // Configures and enables exporting database schemas into JSON files in the
    // given directory.
    arg("room.schemaLocation", "$projectDir/schemas")

    // Enables Gradle incremental annotation processor.
    arg("room.incremental", "true")

    // Configures Room to rewrite queries such that their top star projection is
    // expanded to only contain the columns defined in the DAO method return type.
    arg("room.expandProjection", "true")
}

// https://docs.gradle.org/current/userguide/declaring_dependencies.html
dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.bundles.jetpack.room)
    ksp(libs.jetpack.room.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converters.gson)

    implementation(libs.bundles.jetpack.datastore)
    implementation(libs.bundles.jetpack.leanback)
    implementation(libs.bundles.jetpack.lifecycle)
    implementation(libs.bundles.jetpack.media3)

    implementation(libs.bitcoinj.core)
    implementation(libs.blitz)
    implementation(libs.coil)
    implementation(libs.gson)
    implementation(libs.jetpack.constraintLayout)
    implementation(libs.jetpack.core)
    implementation(libs.jetpack.core.splashScreen)
    implementation(libs.jetpack.fragment)
    implementation(libs.jetpack.navigation.fragment)
    implementation(libs.jetpack.navigation.ui)
    implementation(libs.jetpack.paging)
    implementation(libs.jetpack.startup)
    implementation(libs.jetpack.swipeRefreshLayout)
    implementation(libs.jetpack.tvProvider)
    implementation(libs.jetpack.work.runtime)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.materialDesign3)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.protobuf.javalite)
    implementation(libs.timber)
    implementation(libs.zxing)

    androidTestImplementation(libs.bundles.test.instrumented)
    testImplementation(libs.bundles.test.local)
    testRuntimeOnly(libs.junit5.vintageEngine)
}
