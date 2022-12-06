import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    application
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.7.20"
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


dependencies {
    val ktorVersion = "2.1.3"
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    api(compose.runtime)
    api(compose.foundation)
    api(compose.material)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

application {
    mainClass.set("tictactoe.MainKt")
}

