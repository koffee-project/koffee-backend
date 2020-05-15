package eu.yeger.utility

import eu.yeger.model.dto.Result
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

suspend fun ApplicationCall.respondWithResult(result: Result<*>) {
    when (result.status.value) {
        in 400..499 -> respond(status = result.status, message = result.data ?: result.status)
        else -> respond(status = result.status, message = result.data ?: "")
    }
}
