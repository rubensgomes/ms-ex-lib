rootProject.name = "ex-lib"
include("lib")

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        maven {
            url = uri("https://repo.repsy.io/mvn/rubensgomes/default/")
            credentials {
                username = System.getProperty("repsyUsername")
                password = System.getProperty("repsyPassword")
            }
        }
    }

    versionCatalogs {
        create("ctlg") {
            from("com.rubensgomes:gradle-catalog:0.0.37")
        }
    }
}
