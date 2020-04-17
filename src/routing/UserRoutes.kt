package eu.yeger.routing

import eu.yeger.model.domain.User
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.UserCreationRequest
import eu.yeger.service.UserService
import eu.yeger.utility.respondWithResult
import io.ktor.application.call
import io.ktor.auth.authenticate
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

        authenticate {
            post {
                val userCreationRequest = call.receive<UserCreationRequest>()
                val result = userService.createUser(userCreationRequest)
                call.respondWithResult(result)
            }

            put {
                val user = call.receive<User>()
                val result = userService.updateUser(user)
                call.respondWithResult(result)
            }
        }

        route("{id}") {
            get {
                val id = call.parameters["id"]!!
                val result = userService.getUserById(id)
                call.respondWithResult(result)
            }

            authenticate {
                delete {
                    val id = call.parameters["id"]!!
                    val result = userService.deleteUserById(id)
                    call.respondWithResult(result)
                }

                post("/balance") {
                    val id = call.parameters["id"]!!
                    val funding = call.receive<Funding>()
                    val result = userService.updateBalance(id, funding)
                    call.respondWithResult(result)
                }
            }
        }
    }
}
