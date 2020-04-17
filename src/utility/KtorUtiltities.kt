package eu.yeger.utility

import eu.yeger.model.dto.Result
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

suspend fun ApplicationCall.respondWithResult(result: Result<*>) {
    respond(status = result.status, message = result)
}
