plugins {
    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = "AltarACAPI-Internal-Shims"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

publishing {
    repositories {
        mavenLocal()
    }
    publications.create<MavenPublication>("maven") {
        artifactId = "AltarAC-internal-shims"
        version = project.version.toString()
        from(components["java"])
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}
