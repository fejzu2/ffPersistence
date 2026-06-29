import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import java.util.Properties

plugins {
    `java-library`
    `maven-publish`
}

val envFile = file("C:/Users/jakub/IdeaProjects/.env")
val envProps = Properties().apply {
    if (envFile.exists()) {
        envFile.readLines()
            .filter { it.contains("=") && !it.trim().startsWith("#") }
            .forEach {
                val (key, value) = it.split("=", limit = 2)
                setProperty(key.trim(), value.trim())
            }
    }
}

allprojects {
    group = "pl.fejzu.persistence"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()

        maven("https://repo.fejzu.pl/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.codemc.io/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    dependencies {
        val lombok = "1.18.38"

        compileOnly("org.projectlombok:lombok:$lombok")
        annotationProcessor("org.projectlombok:lombok:$lombok")

        testCompileOnly("org.projectlombok:lombok:$lombok")
                testAnnotationProcessor("org.projectlombok:lombok:$lombok")
    }

    publishing {
        repositories {
            maven {
                name = if (version.toString().endsWith("-SNAPSHOT")) {
                    "reposilite-snapshots"
                } else {
                    "reposilite-releases"
                }

                url = if (version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://repo.fejzu.pl/snapshots")
                } else {
                    uri("https://repo.fejzu.pl/releases")
                }

                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                        ?: envProps.getProperty("MAVEN_USERNAME")

                    password = System.getenv("MAVEN_TOKEN")
                        ?: envProps.getProperty("MAVEN_TOKEN")
                }

                isAllowInsecureProtocol = false
            }
        }

        publications {
            create<MavenPublication>("library") {
                from(components["java"])

                artifactId = project.name
            }
        }
    }

    tasks.register("deleteExistingVersion") {
        group = "publishing"
        description = "Delete existing version from repository before publishing"

        doLast {
            val repoUrl = if (version.toString().endsWith("-SNAPSHOT")) {
                "https://repo.fejzu.pl/snapshots"
            } else {
                "https://repo.fejzu.pl/releases"
            }

            val groupPath = project.group.toString().replace(".", "/")
            val artifactPath = "$groupPath/${project.name}/${project.version}"
            val deleteUrl = "$repoUrl/$artifactPath"

            val username = System.getenv("MAVEN_USERNAME")
                ?: envProps.getProperty("MAVEN_USERNAME")

            val password = System.getenv("MAVEN_TOKEN")
                ?: envProps.getProperty("MAVEN_TOKEN")

            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                println("⚠ Missing Maven credentials, skipping deleteExistingVersion")
                return@doLast
            }

            try {
                val connection = URL(deleteUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE"
                connection.setRequestProperty(
                    "Authorization",
                    "Basic " + Base64.getEncoder()
                        .encodeToString("$username:$password".toByteArray())
                )

                when (val responseCode = connection.responseCode) {
                    200, 204 -> println("✓ Deleted existing version: $deleteUrl")
                    404 -> println("✓ Version does not exist yet: $deleteUrl")
                    else -> println("⚠ Failed to delete version. Response code: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                println("⚠ Could not delete existing version: ${e.message}")
            }
        }
    }

    tasks.named("publish") {
        dependsOn("deleteExistingVersion")
    }
}