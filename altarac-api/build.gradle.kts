import versioning.BuildConfig

plugins {
    `java-library`
    `maven-publish`
    AltarAC.`base-conventions`
}

group = "ac.altarac"
version = rootProject.version
description = "AltarACAPI"

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenLocal()
    maven("https://jitpack.io/")
    maven("https://repo.viaversion.com")
    mavenCentral()
}

dependencies {
    compileOnly(project(":altarac-api:internal-shims"))
    compileOnly(libs.annotations)
    compileOnly(libs.lombokLib)
    annotationProcessor(libs.lombokLib)

    testImplementation(libs.junitJupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications.create<MavenPublication>("maven") {
        version = project.version.toString()
        from(components["java"])
    }
}
