plugins {
    kotlin("jvm")
    id("maven-publish")
    id("java-library")
}

group = "io.phoenix.sulfur"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/PhoenixUHC/Sulfur")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.phoenix.sulfur"
            artifactId = "sulfur-api"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Sulfur")
                description.set("A framework for customizing, creating and managing UHC games with Kotlin")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/PhoenixUHC/Sulfur/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("JanotLeLapin")
                        name.set("Joseph Daly")
                        email.set("joseph300905@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:PhoenixUHC/Sulfur.git")
                    developerConnection.set("scm:git:ssh://github.om:PhoenixUHC/Sulfur.git")
                    url.set("https://github.com/PhoenixUHC/Sulfur/tree/main")
                }
            }
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
