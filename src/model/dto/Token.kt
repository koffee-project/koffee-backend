package eu.yeger.model.dto

/**
 * DTO class for JWTs.
 *
 * @property token The JWT.
 * @property expiration The expiration of this JWT.
 *
 * @author Jan Müller
 */
data class Token(
    val token: String,
    val expiration: Long
)
