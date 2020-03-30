package eu.yeger.routing

import eu.yeger.model.User
import eu.yeger.model.respondWithResult
import eu.yeger.service.UserService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.ktor.ext.inject

fun Routing.userRoutes() {
    val userService: UserService by inject()

    get("/users") {
        val users = userService.getAllUsers()
        call.respond(users)
    }

    get("/users/{name}") {
        val name = call.parameters["name"]!!
        val result = userService.getUserByName(name = name)
        call.respondWithResult(result)
    }

    post("/users") {
        val user = call.receive<User>()
        val result = userService.saveUser(user)
        call.respondWithResult(result)
    }
}
