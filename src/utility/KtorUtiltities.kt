package eu.yeger.utility

import eu.yeger.model.dto.Result
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

suspend fun ApplicationCall.respondWithResult(result: Result<*>) {
    val message = when (result) {
        is Result.Success -> result.data
        is Result.Failure -> result.error
    }
    respond(status = result.status, message = message)
}
