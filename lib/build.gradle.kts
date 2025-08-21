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
 * This is a blueprint Gradle build.gradle.kts file used by Rubens Gomes during the creation of a
 * new Gradle Spring Boot Java development project.
 *
 * @author [Rubens Gomes](https://rubensgomes.com)
 */
plugins {
    id("idea")
    id("maven-publish")
    id("version-catalog")
    id("java-library")
    // org.jetbrains.kotlin.jvm
    alias(libs.plugins.kotlin.jvm)
    // io.freefair.lombok
    alias(libs.plugins.lombok)
    // net.researchgate.release
    alias(libs.plugins.release)
    // com.diffplug.spotless
    alias(libs.plugins.spotless)
    // io.spring.dependency-management
    alias(libs.plugins.spring.dependency.management)
}

// --------------- >>> gradle properties <<< ----------------------------------
// properties used to configure "jar" and "publish" tasks
val group: String by project
val artifact: String by project
val version: String by project
val title: String by project
val license: String by project
val licenseUrl: String by project
val developerEmail: String by project
val developerId: String by project
val developerName: String by project
val scmConnection: String by project
val scmUrl: String by project
val repsyUrl: String by project
// REPSY_USERNAME must be defined as an environment variable
// REPSY_PASSWORD must be defined as an environment variable
val repsyUsername: String? = System.getenv("REPSY_USERNAME")
val repsyPassword: String? = System.getenv("REPSY_PASSWORD")

project.group = group
project.version = version
project.description = description

// --------------- >>> repositories <<< ---------------------------------------

repositories {
    mavenCentral()
    maven { url = uri("https://repo.repsy.io/mvn/rubensgomes/default/") }
}

// --------------- >>> dependencies <<< ---------------------------------------

dependencyManagement {
    imports {
        mavenBom("org.springframework:spring-framework-bom:6.2.9")
    }
}

dependencies {

    // ########## implementation ##############################################
    implementation("org.springframework:spring-web")
    // jakarta.annotation:jakarta.annotation-api
    implementation(libs.jakarta.annotation.api)
    // jakarta.validation:jakarta.validation-api
    implementation(libs.jakarta.validation.api)
    // org.slf4j:slf4j-api
    implementation(libs.slf4j.api)
    // com.rubensgomes:ms-reqresp-lib
    implementation(libs.ms.reqresp.lib)

    // ########## testImplementation ##########################################
    // Logback bundle:
    //  ch.qos.logback:logback-classic
    //  ch.qos.logback:logback-core
    testImplementation(libs.bundles.logback)
    // JUnit Jupiter bundle for tests:
    //   org.junit.jupiter:junit-jupiter-api
    //   org.junit.jupiter:junit-jupiter-engine
    testImplementation(libs.bundles.junit.jupiter)
    // Bean Validation provider for tests:
    //   jakarta.validation:jakarta.validation-api
    //   org.glassfish.expressly:expressly
    //   org.hibernate.validator:hibernate-validator
    testImplementation(libs.bundles.jakarta.bean.validator)

    // ########## testRuntimeOnly    ##########################################
    // JUnit Jupiter Platform Launcher:
    //   org.junit.platform:junit-platform-launcher
    testRuntimeOnly(libs.junit.platform.launcher)
}

/*
 * Used for troubleshooting.
val versionCatalog = versionCatalogs.named("libs")
println("Library aliases: ${versionCatalog.libraryAliases}")
println("Bundle aliases: ${versionCatalog.bundleAliases}")
println("Plugin aliases: ${versionCatalog.pluginAliases}")
*/

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Base Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Gradle base plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/base_plugin.html

// ----------------------------------------------------------------------------
// --------------- >>> Gradle IDEA Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Idea plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/idea_plugin.html

idea {
    module {
        // download javadocs and sources:
        // $ ./gradlew cleanIdea idea
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Java Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Java plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/java_plugin.html

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.AMAZON)
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to project.properties["title"],
                "Implementation-Title" to project.properties["artifact"],
                "Implementation-Version" to project.properties["version"],
                "Implementation-Vendor" to project.properties["developerName"],
                "Built-By" to project.properties["developerId"],
                "Build-Jdk" to System.getProperty("java.home"),
                "Created-By" to
                    "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
            ),
        )
    }
}

tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).addStringOption(
            "Xdoclint:none",
            "-quiet",
        )
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Maven Publish Plugin <<< ------------------------
// NOTE: This section is dedicated to configuring the maven-publich plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/publishing_maven.html

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") { fromResolutionOf("runtimeClasspath") }
                usage("java-runtime") { fromResolutionResult() }
            }

            groupId = project.group.toString()
            artifactId = artifact
            version = project.version.toString()

            from(components["java"])

            pom {
                name = title
                inceptionYear = "2025"
                packaging = "jar"

                licenses {
                    license {
                        name = license
                        url = licenseUrl
                    }
                }

                developers {
                    developer {
                        id = developerId
                        name = developerName
                        email = developerEmail
                    }
                }

                scm {
                    connection = scmConnection
                    developerConnection = scmConnection
                    url = scmUrl
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(repsyUrl)
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
        }
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> com.diffplug.spotless Plugin <<< -----------------------
// NOTE: This section is dedicated to configuring the spotless plugin.
// ----------------------------------------------------------------------------
// https://github.com/diffplug/spotless

spotless {
    java {
        target("src/**/*.java")

        // Use Google Java Format
        googleJavaFormat()

        // Remove unused imports
        removeUnusedImports()

        licenseHeader(
            """
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
             * limitations under the License.
             */
            """.trimIndent(),
        )

        // Custom import order
        importOrder("java", "javax", "org", "com", "")

        // Trim trailing whitespace
        trimTrailingWhitespace()

        // End with newline
        endWithNewline()
    }

    json {
        target("src/**/*.json")
        jackson()
    }

    // Format Kotlin files (if you add any)
    kotlin {
        target("src/**/*.kt")
        ktfmt()
        licenseHeader(
            """
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
             * limitations under the License.
             */
            """.trimIndent(),
        )
        trimTrailingWhitespace()
        endWithNewline()
    }

    // Format Gradle Kotlin DSL build file
    kotlinGradle {
        target("*.gradle.kts")
        // Use .editorconfig for fine-grained control
        ktlint().setEditorConfigPath("../.editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> org.jetbrains.kotlin.jvm Plugin <<< --------------------
// ----------------------------------------------------------------------------
// https://kotlinlang.org/docs/gradle-configure-project.html#kotlin-and-java-sources

kotlin {
    /**
     * Java types used by Kotlin relaxes the null-safety checks. And the Spring Framework provides
     * null-safety annotations that could be potentially used by Kotlin types. Therefore, we need to
     * make jsr305 "strict" to ensure null-safety checks is NOT relaxed in Kotlin when Java
     * annotations, which are Kotlin platform types, are used.
     */
    compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") }
}

tasks.compileKotlin { dependsOn("spotlessApply") }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle JVM Test Suite Plugin <<< -----------------------
// NOTE: This section is dedicated to configuring the JVM Test Suite plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    // WARNING: If a serviceability tool is in use, please run with
    // -XX:+EnableDynamicAgentLoading to hide this warning
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

// ----------------------------------------------------------------------------
// --------------- >>> net.researchgate.release Plugin <<< --------------------
// NOTE: This section is dedicated to configuring the release plugin.
// ----------------------------------------------------------------------------
// https://github.com/researchgate/gradle-release

release {
    with(git) {
        pushReleaseVersionBranch.set("release")
        pushToRemote.set("origin")
        requireBranch.set("main")
    }
}
