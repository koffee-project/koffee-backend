package eu.yeger

import com.fasterxml.jackson.databind.SerializationFeature
import eu.yeger.auth.JWTConfiguration
import eu.yeger.di.databaseModule
import eu.yeger.di.repositoryModule
import eu.yeger.di.serviceModule
import eu.yeger.routing.authenticationRoutes
import eu.yeger.routing.itemRoutes
import eu.yeger.routing.userRoutes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Koin) {
        modules(serviceModule + repositoryModule + databaseModule)
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        jwt {
            realm = JWTConfiguration.realm
            verifier(JWTConfiguration.verifier)
            validate { credential ->
                if (credential.payload.audience.contains(JWTConfiguration.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        route("/") {
            get {
                call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
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
            exception<AuthenticationException> {
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
