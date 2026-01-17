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

// ------------------- Project setup -------------------
rootProject.name = "ms-ex-lib"
include("lib")

// ------------------- Plugin Management -------------------
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    // Fetch releasePluginVersion directly from settings.extra.properties
    val releasePluginVersion = settings.extra.properties["releasePluginVersion"] as? String
        ?: throw GradleException("Property 'releasePluginVersion' not found in gradle.properties")

    plugins {
        id("net.researchgate.release") version releasePluginVersion
    }
}

// ------------------- Global Plugins -------------------
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// ------------------- Dependency Resolution -------------------
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {

    // Helper function to configure GitHub Maven repos with credentials
    fun RepositoryHandler.githubRepo(url: String?) {
        if (!url.isNullOrBlank()) {
            maven {
                setUrl(url)
                credentials {
                    username = System.getenv("GITHUB_USER")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }

    // Fetch GitHub repo URLs directly from settings.extra.properties
    val jvmLibsRepoPackages = settings.extra.properties["jvmLibsRepoPackages"] as? String

    repositories {
        mavenCentral()
        google()
        githubRepo(jvmLibsRepoPackages)
    }

    versionCatalogs {
        create("libs") {
            from("com.rubensgomes:gradle-catalog:0.0.28")
        }
    }
}
