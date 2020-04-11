package eu.yeger.routing

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.routingModule() = routing {
    route("/") {
        get {
            call.respondText("Hello World!", contentType = ContentType.Text.Plain)
        }

        authenticationRoutes()
        userRoutes()
        itemRoutes()
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
            throw cause
        }
    }
}