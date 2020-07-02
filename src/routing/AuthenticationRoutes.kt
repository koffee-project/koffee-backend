package eu.yeger.routing

import eu.yeger.model.dto.Credentials
import eu.yeger.service.UserService
import eu.yeger.utility.respondWithResult
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
