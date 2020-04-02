package eu.yeger.model

import io.ktor.http.HttpStatusCode

data class Result<T>(
    val status: HttpStatusCode,
    val data: T
)
