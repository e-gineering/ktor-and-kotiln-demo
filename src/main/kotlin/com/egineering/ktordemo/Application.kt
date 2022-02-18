package com.egineering.ktordemo

import com.egineering.ktordemo.plugins.configureKoin
import com.egineering.ktordemo.plugins.configureMonitoring
import com.egineering.ktordemo.plugins.configureRouting
import com.egineering.ktordemo.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        watchPaths = listOf("build"),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureRouting()
    configureMonitoring()
    configureSerialization()
}
