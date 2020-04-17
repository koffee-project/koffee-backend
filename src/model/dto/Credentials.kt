package eu.yeger.model.dto

import io.ktor.auth.Credential

class Credentials(
    val id: String,
    val password: String
) : Credential
