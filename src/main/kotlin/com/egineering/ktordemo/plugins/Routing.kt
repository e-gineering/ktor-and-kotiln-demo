package com.egineering.ktordemo.plugins

import com.egineering.ktordemo.services.ApiClient
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun Application.configureRouting() {

    val routing = Routing()

    routing {

//        route("/hello") {
//            get {
//                call.respondText("Yo!")
//            }
//        }

        route("/projectsSync") {
            get {
                call.respond(routing.apiClient.getProjectsSync())
            }
        }

        route("/projects") {
            get {
                call.respond(routing.apiClient.getProjects())
            }
        }
    }
}

class Routing : KoinComponent {

    // Hack. Don't know how to provide Koin-controlled instance in the configuration method.
    val apiClient: ApiClient by inject()
}
