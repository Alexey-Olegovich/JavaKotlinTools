plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(8) }

dependencies {
    //api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jsoup:jsoup:1.16.1")
    api(project(":CommonUtils"))
    testImplementation(kotlin("test"))
}