package eu.yeger.model

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

data class Result<T>(
    val statusCode: HttpStatusCode,
    val data: T
)

suspend fun ApplicationCall.respondWithResult(result: Result<*>) {
    respond(status = result.statusCode, message = result)
}
