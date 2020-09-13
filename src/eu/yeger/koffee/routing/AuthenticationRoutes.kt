package eu.yeger.koffee.routing

import eu.yeger.koffee.model.dto.Credentials
import eu.yeger.koffee.service.UserService
import eu.yeger.koffee.utility.respondWithResult
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.ktor.ext.inject

/**
 * Appends authentication-related routes to the base route.
 *
 * @receiver The base route.
 *
 * @author Jan Müller
 */
fun Route.authenticationRoutes() {
    val userService: UserService by inject()

    post("login") {
        val credentials = call.receive<Credentials>()
        val result = userService.login(credentials)
        call.respondWithResult(result)
    }
}
