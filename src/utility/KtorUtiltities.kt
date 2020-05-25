package eu.yeger.utility

import eu.yeger.model.dto.Result
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

suspend fun ApplicationCall.respondWithResult(result: Result<*>) {
    val message =
    when (result.data) {
        null -> result.error ?: result.status.description
        else -> result.data
    }
    respond(status = result.status, message = message)
}
