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

// project name should match the root folder
rootProject.name = "ms-ex-lib"
// project type should match "app" or "lib" depending on project nature
include("lib")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    // property found in project root gradle.properties
    val releasePluginVersion: String by settings

    plugins {
        id("net.researchgate.release") version releasePluginVersion
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()

        //NOTE: Since we are using GitHub Packages for the Maven Repos, we must
        // define every package Maven repo separately.

        // property found in project root gradle.properties
        val msReqrespLibMavenRepoUrl: String by settings
        val versionCatalogMavenRepoUrl: String by settings

        // URLs of Rubens' GitHub Package Maven repositories.
        maven {
            url = uri(msReqrespLibMavenRepoUrl)
            credentials {
                username = System.getenv("MAVEN_REPO_USERNAME")
                password = System.getenv("MAVEN_REPO_PASSWORD")
            }
        }

        maven {
            url = uri(versionCatalogMavenRepoUrl)
            credentials {
                username = System.getenv("MAVEN_REPO_USERNAME")
                password = System.getenv("MAVEN_REPO_PASSWORD")
            }
        }

    }

    versionCatalogs {
        create("libs") {
            // Rubens' Gradle version catalog to manage the versions of
            // plugins and dependencies used in the Gradle build file.
            from("com.rubensgomes:gradle-catalog:0.0.13")
        }
    }
}
