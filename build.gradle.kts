plugins {
    kotlin("jvm") version "2.0.20"
    id("org.beryx.runtime") version "1.13.1"
}

group = "org.whatever"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass="org.whatever.MainKt"
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

runtime {
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    modules.addAll("java.net.http","jdk.httpserver","java.desktop")
    jpackage{
        //disable installer
        skipInstaller = true
    }
}
