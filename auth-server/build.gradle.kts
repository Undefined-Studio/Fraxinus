apply plugin: 'com.github.johnrengelman.shadow'
apply plugin: 'application'

mainClassName = "io.ktor.server.netty.EngineMain"

repositories {
    mavenLocal()
    jcenter()
    maven { url 'https://kotlin.bintray.com/ktor' }
}

dependencies {
    compile project(":helheim")
    compile 'com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:1.4.2'
}

shadowJar {
    manifest {
        attributes 'Main-Class': mainClassName
    }
}
