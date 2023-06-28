plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
	id("org.javamodularity.moduleplugin")
}


dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
	withSourcesJar()
	withJavadocJar()
}

//modularity.mixedJavaRelease(8, 9)
// as long as multi release jars are not provided by the module plugin, we stick with Java 9 only
// related issue: https://github.com/java9-modularity/gradle-modules-plugin/issues/137
modularity.standardJavaRelease(9)

tasks {
	// this was at the time of writing necessary to get the tests to compile in mixed target mode
	// check if this is still needed when the mixed-build issue is resolved
//	compileTestJava {
//		extensions.configure(CompileTestModuleOptions::class) {
//			isCompileOnClasspath = true
//		}
//	}

	test {
		// Use JUnit Platform for unit tests.
		useJUnitPlatform()
	}
}
