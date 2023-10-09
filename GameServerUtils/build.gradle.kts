plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(8) }

dependencies {
    api(project(":CommonUtils"))

    api("com.badlogicgames.gdx:gdx-box2d:1.12.0")
    api("com.badlogicgames.gdx:gdx:1.12.0")

    //api("dev.dominion.ecs:dominion-ecs-engine:0.9.0")
    api("net.onedaybeard.artemis:artemis-odb:2.3.0")

    api("com.esotericsoftware:kryo:5.4.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

    api("com.google.guava:guava:31.1-jre")

    testImplementation(kotlin("test"))
}