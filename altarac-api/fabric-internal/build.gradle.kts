plugins {
    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = "AltarACAPI-Fabric-Internal"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":altarac-api"))
    api(project(":altarac-api:internal"))

    compileOnly(libs.annotations)
    compileOnly(libs.lombokLib)
    annotationProcessor(libs.lombokLib)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
    options.encoding = "UTF-8"
}

publishing {
    repositories {
        mavenLocal()
    }
    publications.create<MavenPublication>("maven") {
        artifactId = "AltarAC-fabric-internal"
        version = project.version.toString()
        from(components["java"])
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}
