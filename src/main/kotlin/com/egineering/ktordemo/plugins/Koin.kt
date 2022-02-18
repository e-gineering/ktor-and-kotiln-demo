package com.egineering.ktordemo.plugins

import com.egineering.ktordemo.configuration.ApplicationConfiguration
import com.egineering.ktordemo.services.ApiClient
import com.egineering.ktordemo.services.ApiClientImpl
import io.ktor.server.application.*
import io.ktor.util.*
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {

    install(CustomKoinPlugin) {
        slf4jLogger(level = Level.INFO)
        modules(koinModule)
    }
}

val koinModule = module {
    single { ApplicationConfiguration() }
    single<ApiClient> { ApiClientImpl(get()) }
}

// https://github.com/InsertKoinIO/koin/issues/1295
// Copy from the latest koin code and reimport
val KApplicationStarted = EventDefinition<KoinApplication>()
val KApplicationStopPreparing = EventDefinition<KoinApplication>()
val KApplicationStopped = EventDefinition<KoinApplication>()

// Create a new custom application plugin
internal class CustomKoinPlugin(internal val koinApplication: KoinApplication) {

    // Implements ApplicationPlugin as a companion object.
    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, KoinApplication, CustomKoinPlugin> {

        // Creates a unique key for the plugin.
        override val key = AttributeKey<CustomKoinPlugin>("CustomKoinPlugin")

        // Code to execute when installing the plugin.
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: KoinApplication.() -> Unit
        ): CustomKoinPlugin {
            val monitor = pipeline.environment?.monitor
            val koinApplication = startKoin(appDeclaration = configure)
            if (monitor != null) {
                monitor.raise(KApplicationStarted, koinApplication)
                monitor.subscribe(ApplicationStopping) {
                    monitor.raise(KApplicationStopPreparing, koinApplication)
                    stopKoin()
                    monitor.raise(KApplicationStopped, koinApplication)
                }
            }
            return CustomKoinPlugin(koinApplication)
        }
    }
}
