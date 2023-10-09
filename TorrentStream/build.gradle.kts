plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(8) }

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(project(":CommonUtils"))
    testImplementation(kotlin("test"))
}