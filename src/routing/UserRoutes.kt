package eu.yeger.routing

import eu.yeger.model.User
import eu.yeger.service.UserService
import eu.yeger.utility.respondWithResult
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
            val result = userService.createUser(user)
            call.respondWithResult(result)
        }

        put {
            val user = call.receive<User>()
            val result = userService.updateUser(user)
            call.respondWithResult(result)
        }

        route("{id}") {
            get {
                val id = call.parameters["id"]!!
                val result = userService.getUserById(id = id)
                call.respondWithResult(result)
            }

            delete {
                val id = call.parameters["id"]!!
                val result = userService.deleteUserById(id)
                call.respondWithResult(result)
            }
        }
    }
}
