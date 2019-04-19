@file: Suppress("SpellCheckingInspection")

import kotlin.reflect.full.memberProperties

object Dependencies {
    val ktorServerNetty = "io.ktor:ktor-server-netty:${Versions.KTOR_VERSION}"
    val logback = "ch.qos.logback:logback-classic:${Versions.LOGBACK_VERSION}"
    val ktorServerCore = "io.ktor:ktor-server-core:${Versions.KTOR_VERSION}"
    val koinKtor = "org.koin:koin-ktor:${Versions.KOIN_KTOR_VERSION}"
    val ktorJackson = "io.ktor:ktor-jackson:${Versions.KTOR_VERSION}"
    val ktorServerTest = "io.ktor:ktor-server-tests:${Versions.KTOR_VERSION}"
    val concurrentMap = "com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:1.4.2"
    val exposed = "org.jetbrains.exposed:exposed:${Versions.EXPOSED_VERSION}"
    val hikricp = "com.zaxxer:HikariCP:${Versions.HIKARI_VERSION}"
    val mysql  = "mysql:mysql-connector-java:${Versions.MYSQL_VERSION}"
    val coroutineJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.KOTLIN_COROUTINE_VERSION}"
    val htmlJs = "org.jetbrains.kotlinx:kotlinx-html-js:${Versions.KOTLINX_HTML_VERSION}"
    val kotlinReact = "org.jetbrains:kotlin-react:${Versions.REACT_KOTLIN_VERSION}"
    val kotlinReactDom = "org.jetbrains:kotlin-react-dom:${Versions.REACT_KOTLIN_VERSION}"
    val kotlinReactRouterDom = "org.jetbrains:kotlin-react-router-dom:${Versions.REACT_ROUTER_KOTLIN_VERSION}"
    val ktorAuth = "io.ktor:ktor-auth:${Versions.KTOR_VERSION}"
    val ktorAuthJwt = "io.ktor:ktor-auth-jwt:${Versions.KTOR_VERSION}"
}

fun deps(module: String): String {
    val prop = Dependencies::class.memberProperties.firstOrNull {
        it.name.toLowerCase() == module.replace("-", "")
    } ?: throw error("Not Found")

    return prop.getter.call(Dependencies)!! as String
}
