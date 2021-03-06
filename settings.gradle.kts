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

// https://docs.gradle.org/current/userguide/build_lifecycle.html#sec:settings_file

// https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_management
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    // https://docs.gradle.org/current/userguide/custom_plugins.html#note_for_plugins_published_without_java_gradle_plugin
    // https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:plugins_resolution_strategy
    resolutionStrategy {
        eachPlugin {
            // https://github.com/google/dagger/issues/2774#issuecomment-894385513
            if (requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
        }
    }
}

// Declare repositories for all projects, for details, see:
// https://docs.gradle.org/current/userguide/dependency_management.html#sub:centralized-repository-declaration.
dependencyResolutionManagement {
    // Enforce that only settings repositories are used.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    // https://docs.gradle.org/current/userguide/dependency_management.html#declaring-repositories
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

// https://docs.gradle.org/current/userguide/multi_project_builds.html#naming_recommendations
rootProject.name = "LBRYtv"
include(":app")
