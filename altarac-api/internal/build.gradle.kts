plugins {
    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = "AltarACAPI-Internal"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":altarac-api"))

    compileOnly(libs.annotations)
    compileOnly(libs.lombokLib)
    annotationProcessor(libs.lombokLib)

    compileOnly(libs.sqliteJdbc)
    compileOnly(libs.mysqlJdbc)
    compileOnly(libs.postgresJdbc)
    compileOnly(libs.mongoDriverSync)
    compileOnly(libs.jedis)
    compileOnly(libs.hikaricp)

    api(libs.disruptor)

    testImplementation(libs.annotations)
    testImplementation(libs.junitJupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.sqliteJdbc)
    testImplementation(libs.mysqlJdbc)
    testImplementation(libs.postgresJdbc)
    testImplementation(libs.mongoDriverSync)
    testImplementation(libs.jedis)
    testImplementation(libs.hikaricp)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        mavenLocal()
    }
    publications.create<MavenPublication>("maven") {
        artifactId = "AltarAC-internal"
        version = project.version.toString()
        from(components["java"])
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

val codecBindingsDir = layout.buildDirectory.dir("generated/codec-bindings")
val generateCodecBindings = tasks.register<JavaExec>("generateCodecBindings") {
    group = "build"
    description = "Capture v2 codec bindings for builtin persistent records"
    dependsOn(tasks.named("compileJava"))
    classpath = sourceSets["main"].output.classesDirs + sourceSets["main"].compileClasspath
    mainClass.set("ac.altarac.internal.storage.codec.gen.CodecBindingCaptureTool")
    val outFile = codecBindingsDir.get().file("META-INF/AltarAC/codec-bindings.tsv").asFile
    args(outFile.absolutePath)
    outputs.file(outFile)
    outputs.upToDateWhen { false }
}

sourceSets["main"].output.dir(mapOf("builtBy" to generateCodecBindings), codecBindingsDir)
