package com.egineering.ktordemo

import com.egineering.ktordemo.plugins.configureKoin
import com.egineering.ktordemo.plugins.configureMonitoring
import com.egineering.ktordemo.plugins.configureRouting
import com.egineering.ktordemo.plugins.configureSerialization
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureKoin()
        configureRouting()
        configureMonitoring()
        configureSerialization()
    }.start(wait = true)
}
