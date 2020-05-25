package eu.yeger.model.dto

import io.ktor.http.HttpStatusCode

sealed class Result<Data : Any>(
    val status: HttpStatusCode,
    val data: Data? = null,
    val error: String? = null
) {
    class Conflict<Data : Any>(data: Data) : Result<Data>(HttpStatusCode.Conflict, data)

    class Created<Data : Any>(data: Data) : Result<Data>(HttpStatusCode.Created, data)

    class Forbidden<Data : Any>(error: String) : Result<Data>(HttpStatusCode.Forbidden, error = error)

    class NotFound<Data : Any>(error: String) : Result<Data>(HttpStatusCode.NotFound, error = error)

    class OK<Data : Any>(data: Data) : Result<Data>(HttpStatusCode.OK, data)

    class Unauthorized<Data : Any>(error: String) : Result<Data>(HttpStatusCode.Unauthorized, error = error)

    class UnprocessableEntity<Data : Any>(error: String) : Result<Data>(HttpStatusCode.UnprocessableEntity, error = error)
}
