plugins {
    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = "AltarACAPI-Bukkit-Internal"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":altarac-api"))
    api(project(":altarac-api:internal"))

    compileOnly(libs.paper.api)
    compileOnly(libs.annotations)
    compileOnly(libs.lombokLib)
    annotationProcessor(libs.lombokLib)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
    options.encoding = "UTF-8"
}

publishing {
    repositories {
        mavenLocal()
    }
    publications.create<MavenPublication>("maven") {
        artifactId = "AltarAC-bukkit-internal"
        version = project.version.toString()
        from(components["java"])
    }
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}
