package eu.yeger.routing

import eu.yeger.model.User
import eu.yeger.model.respondWithResult
import eu.yeger.service.UserService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userService: UserService by inject()

    route("users") {
        get {
            val users = userService.getAllUsers()
            call.respondWithResult(users)
        }

        post {
            val user = call.receive<User>()
            val result = userService.saveUser(user)
            call.respondWithResult(result)
        }

        route("{name}") {
            get {
                val name = call.parameters["name"]!!
                val result = userService.getUserByName(name = name)
                call.respondWithResult(result)
            }
        }
    }
}
