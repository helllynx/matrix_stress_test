plugins {
    kotlin("jvm") version "1.4.10"
}

group = "org.helllynx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("org.slf4j:log4j-over-slf4j:1.7.25")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("commons-io:commons-io:2.6")
    implementation("commons-codec:commons-codec:1.11")
    implementation("com.squareup.okhttp3:okhttp:3.12.0")
    implementation("net.sourceforge.streamsupport:streamsupport:1.7.0")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("net.i2p.crypto:eddsa:0.3.0")
}
