plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(8) }

dependencies {
    api(project(":GameServerUtils"))
    api("com.badlogicgames.gdx:gdx-freetype:1.12.0")

    testImplementation(kotlin("test"))
}