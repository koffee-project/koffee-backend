package eu.yeger.routing

import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Purchase
import eu.yeger.service.TransactionService
import eu.yeger.service.UserService
import eu.yeger.utility.respondWithResult
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userService: UserService by inject()
    val transactionService: TransactionService by inject()

    route("users") {
        get {
            val users = userService.getAllUsers()
            call.respondWithResult(users)
        }

        authenticate {
            post {
                val partialUser = call.receive<PartialUser>()
                val result = userService.createUser(partialUser)
                call.respondWithResult(result)
            }

            put {
                val partialUser = call.receive<PartialUser>()
                val result = userService.updateUser(partialUser)
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

                post("funding") {
                    val id = call.parameters["id"]!!
                    val funding = call.receive<Funding>()
                    val result = transactionService.processFunding(id, funding)
                    call.respondWithResult(result)
                }
            }

            route("purchases") {
                post {
                    val id = call.parameters["id"]!!
                    val purchase = call.receive<Purchase>()
                    val result = transactionService.processPurchase(id, purchase)
                    call.respondWithResult(result)
                }

                post("refund") {
                    val id = call.parameters["id"]!!
                    val result = transactionService.refundLastPurchase(id)
                    call.respondWithResult(result)
                }
            }

            get("transactions") {
                val id = call.parameters["id"]!!
                val result = transactionService.getTransactionsOfUser(id)
                call.respondWithResult(result)
            }
        }
    }
}
