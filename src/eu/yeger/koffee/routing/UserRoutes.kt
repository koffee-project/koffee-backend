package eu.yeger.koffee.routing

import eu.yeger.koffee.model.dto.Funding
import eu.yeger.koffee.model.dto.PartialUser
import eu.yeger.koffee.model.dto.Purchase
import eu.yeger.koffee.service.ProfileImageService
import eu.yeger.koffee.service.TransactionService
import eu.yeger.koffee.service.UserService
import eu.yeger.koffee.utility.encodeBase64
import eu.yeger.koffee.utility.respondWithResult
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import org.koin.ktor.ext.inject

/**
 * Appends user-related routes to the base route.
 *
 * @receiver The base route.
 *
 * @author Jan Müller
 */
fun Route.userRoutes() {
    val profileImageService: ProfileImageService by inject()
    val transactionService: TransactionService by inject()
    val userService: UserService by inject()

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

            route("image") {
                get {
                    val id = call.parameters["id"]!!
                    val result = profileImageService.getProfileImageByUserId(id)
                    call.respondWithResult(result)
                }

                get("timestamp") {
                    val id = call.parameters["id"]!!
                    val result = profileImageService.getProfileImageTimestampByUserId(id)
                    call.respondWithResult(result)
                }

                post {
                    val id = call.parameters["id"]!!
                    val encodedImage = call.receiveMultipart()
                        .readAllParts()
                        .fold(byteArrayOf()) { acc, part ->
                            when (part) {
                                is PartData.FileItem -> acc + part.streamProvider().readBytes()
                                else -> acc
                            }.also { part.dispose() }
                        }.encodeBase64()
                    val result = profileImageService.saveProfileImageForUser(id, encodedImage)
                    call.respondWithResult(result)
                }

                delete {
                    val id = call.parameters["id"]!!
                    val result = profileImageService.deleteProfileImageByUserId(id)
                    call.respondWithResult(result)
                }
            }
        }
    }
}
