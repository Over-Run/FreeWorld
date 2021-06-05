import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.31"
    idea
}

fun setArchivesBaseName(name: String) {
    setProperty("archivesBaseName", name)
}

group = "io.github.over-run"
setArchivesBaseName("freeworld")
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(platform("org.lwjgl:lwjgl-bom:3.2.3"))
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")

    for (depend in arrayOf("", "-glfw", "-opengl")) {
        implementation("org.lwjgl", "lwjgl$depend")
        for (native in arrayOf("windows", "windows-x86", "macos", "linux", "linux-arm64", "linux-arm32")) {
            runtimeOnly("org.lwjgl", "lwjgl$depend", classifier = "natives-$native")
        }
    }
    implementation("org.joml", "joml", "1.10.1")

    compileOnly("org.jetbrains:annotations:20.1.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

idea.module.inheritOutputDirs = true