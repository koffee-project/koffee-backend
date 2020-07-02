package eu.yeger.model.dto

import eu.yeger.model.domain.User
import io.ktor.auth.Credential

/**
 * Login-credentials.
 *
 * @property id The [User]'s id.
 * @property password The [User]'s password.
 */
class Credentials(
    val id: String,
    val password: String
) : Credential
