package eu.yeger.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import eu.yeger.model.User
import java.util.Date

object JWTConfiguration {
    // TODO change this to actual secret
    private const val secret = "secret"

    private const val issuer = "yeger.eu"
    const val audience = "jwt-audience"
    const val realm = "koffee-backend"

    private val algorithm = Algorithm.HMAC256(secret)

    private const val duration = 3_600_000 * 24 // 24 hours

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun makeToken(user: User): String? =
        when (user.isAdmin) {
            true -> JWT.create()
                .withSubject("Authentication")
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", user.id)
                .withExpiresAt(getExpiration())
                .sign(algorithm)
            else -> null
        }

    private fun getExpiration() = Date(System.currentTimeMillis() + duration)
}
