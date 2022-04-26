plugins {
    kotlin("jvm")
    id("maven-publish")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.phoenix.sulfur"
            artifactId = "sulfur-api"

            from(components["java"])
        }
    }
}
