package eu.yeger.model.dto

import io.ktor.http.HttpStatusCode

sealed class Result<T : Any>(
    val status: HttpStatusCode,
    val data: T? = null,
    val error: String? = null
) {
    class Conflict<T : Any>(error: String) : Result<T>(HttpStatusCode.Conflict, error = error)

    class Created<T : Any>(data: T) : Result<T>(HttpStatusCode.Created, data)

    class Forbidden<T : Any>(error: String) : Result<T>(HttpStatusCode.Forbidden, error = error)

    class NotFound<T : Any>(error: String) : Result<T>(HttpStatusCode.NotFound, error = error)

    class OK<T : Any>(data: T) : Result<T>(HttpStatusCode.OK, data)

    class Unauthorized<T : Any>(error: String) : Result<T>(HttpStatusCode.Unauthorized, error = error)

    class UnprocessableEntity<T : Any>(error: String) : Result<T>(HttpStatusCode.UnprocessableEntity, error = error)
}

private class Success<T : Any>(status: HttpStatusCode, data: T) : Result<T>(status, data = data)

private class Error<T : Any>(status: HttpStatusCode, error: String?) : Result<T>(status, error = error)

suspend fun <T : Any, U : Any> Result<T>.map(transformation: suspend (T) -> U): Result<U> {
    return when (data) {
        null -> Error(status, error)
        else -> Success(status, transformation(data))
    }
}

suspend fun <T : Any> Result<T>.mapErrorStatus(transformation: suspend (HttpStatusCode) -> HttpStatusCode): Result<T> {
    return when (error) {
        null -> this
        else -> Error(transformation(status), error)
    }
}

suspend fun <T : Any, U : Any> Result<T>.andThen(transformation: suspend (T) -> Result<U>): Result<U> {
    return when (error) {
        null -> transformation(data!!)
        else -> Error(status, error)
    }
}

suspend fun <T : Any> Result<T>.withResult(consumer: suspend (T) -> Unit): Result<T> {
    if (data !== null) {
        consumer(data)
    }
    return this
}
