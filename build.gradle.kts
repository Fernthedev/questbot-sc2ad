import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    id("com.google.devtools.ksp").version("1.9.20-1.0.14") // Or latest version of KSP
    application
    distribution
}

group = "com.github.fernthedev.questbot"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.inject:guice:7.0.0")

    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-moshi:2.3.1")

    implementation("org.reflections:reflections:0.10.2")

    implementation("org.javacord:javacord:3.8.0")

    runtimeOnly("org.apache.logging.log4j:log4j-core:2.21.1")
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.21.1")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.21.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("questbot.MainKt")
}