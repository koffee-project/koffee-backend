package eu.yeger.model.dto

import io.ktor.http.HttpStatusCode

sealed class Result<T : Any>(
    val status: HttpStatusCode
) {
    class Success<T : Any>(val data: T, status: HttpStatusCode) : Result<T>(status)

    class Failure<T : Any>(val error: String, status: HttpStatusCode) : Result<T>(status)

    companion object {
        fun <T : Any> conflict(error: String) = Failure<T>(error, HttpStatusCode.Conflict)

        fun <T : Any> created(data: T) = Success(data, HttpStatusCode.Created)

        fun <T : Any> forbidden(error: String) = Failure<T>(error, HttpStatusCode.Forbidden)

        fun <T : Any> notFound(error: String) = Failure<T>(error, HttpStatusCode.NotFound)

        fun <T : Any> ok(data: T) = Success(data, HttpStatusCode.OK)

        fun <T : Any> unauthorized(error: String) = Failure<T>(error, HttpStatusCode.Unauthorized)

        fun <T : Any> unprocessableEntity(error: String) = Failure<T>(error, HttpStatusCode.UnprocessableEntity)
    }
}

suspend fun <T : Any, U : Any> Result<T>.map(transformation: suspend (T) -> U): Result<U> {
    return when (this) {
        is Result.Success -> Result.Success(transformation(data), status)
        is Result.Failure -> Result.Failure(error, status)
    }
}

suspend fun <T : Any> Result<T>.mapFailureStatus(transformation: suspend (HttpStatusCode) -> HttpStatusCode): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> Result.Failure(error, transformation(status))
    }
}

suspend fun <T : Any, U : Any> Result<T>.andThen(transformation: suspend (T) -> Result<U>): Result<U> {
    return when (this) {
        is Result.Success -> transformation(data)
        is Result.Failure -> Result.Failure(error, status)
    }
}

suspend fun <T : Any> Result<T>.withResult(consumer: suspend (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        consumer(data)
    }
    return this
}
