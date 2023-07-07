import org.gradle.plugins.signing.signatory.internal.gnupg.GnupgSignatoryProvider
import org.gradle.security.internal.gnupg.GnupgSignatory

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
	id("org.javamodularity.moduleplugin")
	// publishing
	`maven-publish`
	signing
}

version = rootProject.version


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

artifacts {
	archives(tasks.getByName("javadocJar"))
	archives(tasks.getByName("sourcesJar"))
}


signing {
	// override provider, so we can set the prefix for the signing key property
	val keyPrefix = "ellog"
	val sigProv = object : GnupgSignatoryProvider() {
		override fun getDefaultSignatory(project: Project?): GnupgSignatory? {
			return getSignatory(keyPrefix)
		}
	}
	sigProv.createSignatoryFor(project, keyPrefix, emptyArray())
	signatories = sigProv


	sign(publishing.publications)
}

publishing {
	repositories {
		maven {
			name = "mavenCentral"
			val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
			credentials {
				username = System.getenv("OSSRH_USERNAME") ?: project.findProperty("ossrhUsernameEllog") as String?
				password = System.getenv("OSSRH_PASSWORD") ?: project.findProperty("ossrhPasswordEllog") as String?
			}
		}
	}

	publications {
		create<MavenPublication>("mavenJava") {

			//from(components["java"])
			artifact(tasks.getByName("jar"))
			artifact(tasks.getByName("javadocJar"))
			artifact(tasks.getByName("sourcesJar"))

			pom {
				groupId = rootProject.group.toString()
				artifactId = "uuid4j"
				packaging = "jar"

				name.set("UUID4J")
				description.set("UUID4J is a Java library for generating and reading of UUIDs (version 1, 3, 4, 5, 6, and 7).")
				url.set("https://github.com/sake/uuid4j")

				scm {
					connection.set("scm:git:https://github.com/sake/uuid4j.git")
					developerConnection.set("scm:git:ssh://git@github.com/sake/uuid4j.git")
					url.set("https://github.com/sake/uuid4j")
				}

				licenses {
					license {
						name.set("LGPL-3.0-or-later")
						url.set("https://www.gnu.org/licenses/lgpl+gpl-3.0.txt")
					}
				}

				developers {
					developer {
						name.set("Tobias Wich")
						email.set("tobias.wich@electrologic.org")
					}
				}
			}
		}
	}
}
