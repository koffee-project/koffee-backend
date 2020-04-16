package eu.yeger.model

import io.ktor.http.HttpStatusCode

sealed class Result<Data>(
    val status: HttpStatusCode,
    val data: Data
) {
    class Conflict<Data>(data: Data) : Result<Data>(HttpStatusCode.Conflict, data)

    class Created<Data>(data: Data) : Result<Data>(HttpStatusCode.Created, data)

    class Forbidden<Data>(data: Data) : Result<Data>(HttpStatusCode.Forbidden, data)

    class NotFound<Data>(data: Data) : Result<Data>(HttpStatusCode.NotFound, data)

    class OK<Data>(data: Data) : Result<Data>(HttpStatusCode.OK, data)

    class Unauthorized<Data>(data: Data) : Result<Data>(HttpStatusCode.Unauthorized, data)

    class UnprocessableEntity<Data>(data: Data) : Result<Data>(HttpStatusCode.UnprocessableEntity, data)
}
