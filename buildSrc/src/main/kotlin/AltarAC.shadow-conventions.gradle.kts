import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import versioning.BuildConfig

plugins {
    id("com.gradleup.shadow")
}

tasks.named<ShadowJar>("shadowJar") {
    minimize {
        // adventure's DataComponentValueConverter gson provider is only referenced via
        // ServiceLoader, so minimize() strips it and adventure's static init then throws
        // (ServiceConfigurationError) on enable. Keep the gson serializer's classes.
        exclude(dependency("net.kyori:adventure-text-serializer-gson:.*"))
    }
    archiveFileName = "${rootProject.name}-${project.name}-${rootProject.version}.jar"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    if (BuildConfig.relocate) {
        if (BuildConfig.shadePE) {
            relocate("io.github.retrooper.packetevents", "ac.altarac.shaded.io.github.retrooper.packetevents")
            relocate("com.github.retrooper.packetevents", "ac.altarac.shaded.com.github.retrooper.packetevents")
            relocate("net.kyori", "ac.altarac.shaded.kyori") // use PE's built-in adventure instead when not shading PE
        }
        relocate("club.minnced", "ac.altarac.shaded.discord-webhooks")
        relocate("org.slf4j", "ac.altarac.shaded.slf4j") // Required by discord-webhooks
        relocate("github.scarsz.configuralize", "ac.altarac.shaded.configuralize")
        relocate("com.github.puregero", "ac.altarac.shaded.com.github.puregero")
        relocate("com.google.code.gson", "ac.altarac.shaded.gson")
        relocate("alexh", "ac.altarac.shaded.maps")
        relocate("it.unimi.dsi.fastutil", "ac.altarac.shaded.fastutil")
        relocate("okhttp3", "ac.altarac.shaded.okhttp3")
        relocate("okio", "ac.altarac.shaded.okio")
        relocate("org.yaml.snakeyaml", "ac.altarac.shaded.snakeyaml")
        relocate("org.json", "ac.altarac.shaded.json")
        relocate("org.intellij", "ac.altarac.shaded.intellij")
        relocate("org.jetbrains", "ac.altarac.shaded.jetbrains")
        relocate("org.incendo", "ac.altarac.shaded.incendo")
        relocate("io.leangen.geantyref", "ac.altarac.shaded.geantyref") // Required by cloud
        relocate("com.zaxxer", "ac.altarac.shaded.zaxxer") // Database history
    }
    mergeServiceFiles()
}

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}
