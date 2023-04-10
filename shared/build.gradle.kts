import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    `java-library`
}

group = "net.codebot"
version = "1.0.0"

val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDirectory.set(compileKotlin.destinationDirectory)

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    // Jackson core library
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.1")

    // Jackson databind module for JSON serialization/deserialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")

    // Jackson module for Kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")

    // Swing for png export
    implementation("org.openjfx:javafx-swing:16")

    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20210307")

    // iText for pdf export
    implementation("com.itextpdf:itextpdf:5.5.13.2")

    testImplementation(kotlin("test"))
}
