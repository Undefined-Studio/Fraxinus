@file: Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

repositories {
    maven {
        url = uri("http://dl.bintray.com/kotlin/kotlin-js-wrappers")
    }
}


dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(deps("coroutine-js"))
    implementation(deps("html-js"))
    implementation(deps("kotlin-react"))
    implementation(deps("kotlin-react-dom"))
    implementation(deps("kotlin-react-router-dom"))
    implementation(deps("kotlinx-serialization-runtime-js"))
    implementation(deps("kotlin-redux"))
    implementation(deps("kotlin-react-redux"))
    testImplementation(kotlin("test-js"))
}

kotlinFrontend {
    sourceMaps = false

    npm {
        dependency("react")
        dependency("react-dom")
        dependency("react-router-dom")

        devDependency("css-loader")
        devDependency("style-loader")
        devDependency("babel-loader")
        devDependency("@babel/core")
        devDependency("karma")
        devDependency("html-webpack-plugin")
    }

    bundle("webpack", delegateClosureOf<WebPackExtension> {
        contentPath = file("build")
        port = 8080
    })
}

tasks.withType<Kotlin2JsCompile> {
    kotlinOptions {
        moduleKind = "commonjs"
        sourceMap = true
        metaInfo = true
        main = "call"
        outputFile = "${project.buildDir.path}/js/${project.name}.js"
    }
}