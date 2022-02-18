package com.egineering.ktordemo.configuration

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.koin.core.component.KoinComponent

class ApplicationConfiguration : KoinComponent {
    
    private val config: Config = ConfigFactory.load()
    val apiUrl: String = config.getString("ktor.application.api-url")
}
