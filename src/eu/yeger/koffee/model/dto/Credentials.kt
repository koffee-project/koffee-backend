package eu.yeger.koffee.model.dto

import eu.yeger.koffee.model.domain.User
import io.ktor.auth.Credential

/**
 * Login-credentials.
 *
 * @property id The [User]'s id.
 * @property password The [User]'s password.
 *
 * @author Jan Müller
 */
class Credentials(
    val id: String,
    val password: String
) : Credential
