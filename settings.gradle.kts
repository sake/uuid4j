plugins {
	// Apply the foojay-resolver plugin to allow automatic download of JDKs
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

dependencyResolutionManagement {
	repositories {
		// Use Maven Central for resolving dependencies.
		mavenCentral()
	}
}

rootProject.name = "uuid4j"
include("lib")
