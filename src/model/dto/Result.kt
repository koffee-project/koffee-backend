package eu.yeger.model.dto

import io.ktor.http.HttpStatusCode

/**
 * Functional Result class with HTTP status information.
 *
 * @param T The type of the data.
 * @property status The HTTP status code.
 *
 * @author Jan Müller
 */
sealed class Result<T : Any>(
    val status: HttpStatusCode
) {

    /**
     * [Result] class for successful operations.
     *
     * @param T The type of the data.
     * @param status The HTTP status code.
     * @property data The data of the [Result].
     *
     * @author Jan Müller
     */
    class Success<T : Any>(val data: T, status: HttpStatusCode) : Result<T>(status)

    /**
     * [Result] class for unsuccessful operations.
     *
     * @param T The type of the [Result].
     * @param status The HTTP status code.
     * @property error The error of the [Result].
     *
     * @author Jan Müller
     */
    class Failure<T : Any>(val error: String, status: HttpStatusCode) : Result<T>(status)

    companion object {

        /**
         * Returns a [Failure] for the conflict status code.
         *
         * @param T The type of the [Result].
         * @param error The error text.
         *
         * @author Jan Müller
         */
        fun <T : Any> conflict(error: String) = Failure<T>(error, HttpStatusCode.Conflict)

        /**
         * Returns a [Success] for the created status code.
         *
         * @param T The type of the [Result].
         * @param data The data of the [Result].
         *
         * @author Jan Müller
         */
        fun <T : Any> created(data: T) = Success(data, HttpStatusCode.Created)

        /**
         * Returns a [Failure] for the forbidden status code.
         *
         * @param T The type of the [Result].
         * @param error The error text.
         *
         * @author Jan Müller
         */
        fun <T : Any> forbidden(error: String) = Failure<T>(error, HttpStatusCode.Forbidden)

        /**
         * Returns a [Failure] for the not found status code.
         *
         * @param T The type of the [Result].
         * @param error The error text.
         *
         * @author Jan Müller
         */
        fun <T : Any> notFound(error: String) = Failure<T>(error, HttpStatusCode.NotFound)

        /**
         * Returns a [Success] for the ok status code.
         *
         * @param T The type of the [Result].
         * @param data The data of the [Result].
         *
         * @author Jan Müller
         */
        fun <T : Any> ok(data: T) = Success(data, HttpStatusCode.OK)

        /**
         * Returns a [Failure] for the unauthorized status code.
         *
         * @param T The type of the [Result].
         * @param error The error text.
         *
         * @author Jan Müller
         */
        fun <T : Any> unauthorized(error: String) = Failure<T>(error, HttpStatusCode.Unauthorized)

        /**
         * Returns a [Failure] for the unprocessable entity status code.
         *
         * @param T The type of the [Result].
         * @param error The error text.
         *
         * @author Jan Müller
         */
        fun <T : Any> unprocessableEntity(error: String) = Failure<T>(error, HttpStatusCode.UnprocessableEntity)
    }
}

/**
 * Transforms the data of [Result]s if they are [Result.Success]es.
 *
 * @param T The source type.
 * @param U The target type.
 * @param transformation The transformation operation.
 * @return The new [Result].
 *
 * @author Jan Müller
 */
suspend fun <T : Any, U : Any> Result<T>.map(transformation: suspend (T) -> U): Result<U> {
    return when (this) {
        is Result.Success -> Result.Success(transformation(data), status)
        is Result.Failure -> Result.Failure(error, status)
    }
}

/**
 * Transforms the failure status of [Result]s if they are [Result.Failure]s.
 *
 * @param T The [Result] type.
 * @param transformation The transformation operation.
 * @return The new [Result].
 *
 * @author Jan Müller
 */
suspend fun <T : Any> Result<T>.mapFailureStatus(transformation: suspend (HttpStatusCode) -> HttpStatusCode): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> Result.Failure(error, transformation(status))
    }
}

/**
 * Transforms the data of [Result]s if they are [Result.Success]es.
 *
 * @param T The source type.
 * @param U The target type.
 * @param transformation The transformation operation. Can be used to turn into [Result.Failure]s.
 * @return The new [Result].
 *
 * @author Jan Müller
 */
suspend fun <T : Any, U : Any> Result<T>.andThen(transformation: suspend (T) -> Result<U>): Result<U> {
    return when (this) {
        is Result.Success -> transformation(data)
        is Result.Failure -> Result.Failure(error, status)
    }
}

/**
 * Consumes the data of [Result]s if they are [Result.Success]es.
 *
 * @param T The source type.
 * @param consumer The consuming operation.
 * @return The identity of the [Result].
 *
 * @author Jan Müller
 */
suspend fun <T : Any> Result<T>.withResult(consumer: suspend (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        consumer(data)
    }
    return this
}
