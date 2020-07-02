package eu.yeger.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import eu.yeger.Arguments
import eu.yeger.authentication.JWTConfiguration.audience
import eu.yeger.authentication.JWTConfiguration.realm
import eu.yeger.authentication.JWTConfiguration.verifier
import eu.yeger.model.domain.User
import eu.yeger.model.dto.Token
import eu.yeger.utility.readDockerSecret
import java.util.Date

/**
 * The configuration used by the authentication module.
 *
 * @property audience The JWT audience.
 * @property realm The JWT realm.
 * @property verifier The JWT verifier.
 *
 * @author Jan Müller
 */
object JWTConfiguration {

    private val issuer = Arguments.url
    const val audience = "jwt-audience"
    const val realm = "koffee-backend"

    private val algorithm = Algorithm.HMAC256(readDockerSecret(Arguments.hmacSecret) ?: "secret")

    private const val duration = 3_600_000 * 24 // 24 hours

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    /**
     * Generates a [Token] for a [User].
     *
     * @param user The [User] that owns the token.
     * @return The generated [Token] or null if [user] is not an admin.
     */
    fun makeToken(user: User): Token? {
        val expiration = Date(System.currentTimeMillis() + duration)
        val token = when (user.isAdmin) {
            true -> JWT.create()
                .withSubject("Authentication")
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", user.id)
                .withExpiresAt(expiration)
                .sign(algorithm)
            false -> return null
        }
        return Token(token, expiration.time)
    }
}
