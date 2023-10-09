plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(8) }

dependencies {
    api("net.java.dev.jna:jna-platform:5.13.0")
    api("net.java.dev.jna:jna:5.13.0")

    testImplementation(kotlin("test"))
}