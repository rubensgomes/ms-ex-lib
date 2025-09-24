/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the__LICENSE] [1].
 */

/**
 * This is a blueprint Gradle settings.gradle.kts file used by Rubens Gomes
 * during the creation of a new Gradle Java developement project.
 *
 * @author [Rubens Gomes](https://rubensgomes.com)
 */

// The project name should match the root folder
rootProject.name = "ms-ex-lib"
// The project type should match "app" or "lib" depending on project nature
include("lib")

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        // URLs of Rubens' public GitHub Package Maven repositories.
        maven {
            url = uri("https://maven.pkg.github.com/rubensgomes/gradle-catalog")
        }
    }

    versionCatalogs {
        create("libs") {
            // This is Rubens' Gradle version catalog to manage the versions of
            // plugins and dependencies used in a Gradle build file.
            // NOTE: You should replace with our own Gradle Version catalog. Rubens may
            // deactivate this Gradle version catalog at anytime without notice.
            from("com.rubensgomes:gradle-catalog:0.0.10")
        }
    }
}
