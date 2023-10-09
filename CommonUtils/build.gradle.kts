plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

kotlin { jvmToolchain(8) }

tasks{
    shadowJar {
        manifest {
            attributes["Main-Class"] = "alexey.tools.common.mods.DesktopModdedApplicationRunnerKt"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}