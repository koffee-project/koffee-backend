package eu.yeger.koffee.utility

import eu.yeger.koffee.model.dto.Result
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import java.util.Base64

/**
 * Responds to a call using the [Result] and its status code.
 * Uses the data for [Result.Success]es and error for [Result.Failure]s.
 *
 * @receiver The target call.
 * @param result The [Result] used for responding.
 *
 * @author Jan Müller
 */
suspend fun ApplicationCall.respondWithResult(result: Result<*>) {
    val message = when (result) {
        is Result.Success -> result.data
        is Result.Failure -> result.error
    }
    respond(status = result.status, message = message)
}

/**
 * Encodes a [ByteArray] as Base64.
 *
 * @receiver The source [ByteArray].
 * @return The encoded [String].
 *
 * @author Jan Müller
 */
fun ByteArray.encodeBase64(): String = Base64.getEncoder().encodeToString(this)
