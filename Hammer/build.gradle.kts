plugins {
    kotlin("jvm")
}

kotlin { jvmToolchain(8) }

dependencies {
    api(project(":CommonUtils"))

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api("org.apache.commons:commons-compress:1.21")
    api("org.apache.commons:commons-lang3:3.1")
    api("org.apache.httpcomponents:httpclient:4.5.13")
    api("org.json:json:20090211")
    api("org.slf4j:slf4j-api:1.7.5")
    api("org.slf4j:slf4j-simple:1.7.5")


    testImplementation(kotlin("test"))
}