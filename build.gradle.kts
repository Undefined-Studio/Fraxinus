//buildscript {
//    repositories {
//        jcenter()
//    }
//
//    dependencies {
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//        classpath 'com.github.jengelman.gradle.plugins:shadow:2.0.4'
//    }
//}

group 'com.udstu'
version '0.1.0'

//subprojects {
//    
//    repositories {
//        mavenLocal()
//        jcenter()
//        maven { url 'https://kotlin.bintray.com/ktor' }
//    }
//    
//    if (project.name != "midgard") {
//        apply plugin: 'kotlin'
//        
//        dependencies {
//            compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
//            compile "io.ktor:ktor-server-netty:$ktor_version"
//            compile "ch.qos.logback:logback-classic:$logback_version"
//            compile "io.ktor:ktor-server-core:$ktor_version"
//            compile "org.koin:koin-ktor:$koin_ktor_version"
//            compile "io.ktor:ktor-jackson:$ktor_version"
//            testCompile "io.ktor:ktor-server-tests:$ktor_version"
//        }
//
//        compileKotlin {
//            kotlinOptions.jvmTarget = java_version
//        }
//        compileTestKotlin {
//            kotlinOptions.jvmTarget = java_version
//        }
//    }
//}