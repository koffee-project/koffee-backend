package eu.yeger.koffee.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import eu.yeger.koffee.Arguments
import eu.yeger.koffee.authentication.JWTConfiguration.audience
import eu.yeger.koffee.authentication.JWTConfiguration.realm
import eu.yeger.koffee.authentication.JWTConfiguration.verifier
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Token
import eu.yeger.koffee.utility.loadDockerSecrets
import java.util.Date

/**
 * The configuration used by the eu.yeger.koffee.authentication module.
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

    private val algorithm = Algorithm.HMAC256(loadDockerSecrets(Arguments.koffeeSecret)["HMAC_SECRET"] ?: "secret")

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
