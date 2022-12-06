import buildutils.configureDetekt
import buildutils.createDetektTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.research.code.submissions.clustering.buildutils.configureDiktat
import org.jetbrains.research.code.submissions.clustering.buildutils.createDiktatTask

plugins {
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

group = "org.example"
version = "1.0-SNAPSHOT"

subprojects {
    apply {
        plugin("java")
        plugin("kotlin")
    }

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    configureDiktat()
    configureDetekt()
}

createDiktatTask()
createDetektTask()