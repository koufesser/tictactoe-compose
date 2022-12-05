plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
