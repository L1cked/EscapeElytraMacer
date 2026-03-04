plugins {
    id("fabric-loom") version "1.14.10"
    kotlin("jvm") version "2.3.10"
    id("maven-publish")
}

group = project.property("maven_group") as String

version = project.property("mod_version") as String
val modVersion = version.toString()

base { archivesName.set(project.property("archives_base_name") as String) }

repositories {
    maven("https://maven.fabricmc.net/")
    // Mod Menu / Terraformers repo (compileOnly API)
    maven("https://maven.terraformersmc.com/releases/")
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation(
            "net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}"
    )

    // Optional integration: compile against Mod Menu API, but don't require it at runtime
    modCompileOnly("com.terraformersmc:modmenu:${project.property("modmenu_version")}")
    // Nice for dev: automatically loads Mod Menu in runClient (still optional for users)
    modLocalRuntime("com.terraformersmc:modmenu:${project.property("modmenu_version")}")
}

tasks.processResources {
    inputs.property("version", modVersion)
    inputs.property("minecraft_version", project.property("minecraft_version") as String)
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to modVersion,
                "minecraft_version" to (project.property("minecraft_version") as String)
            )
        )
    }
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withSourcesJar()
}

kotlin { jvmToolchain(21) }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) }
}
