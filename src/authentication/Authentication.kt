package eu.yeger.authentication

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt

fun Application.authenticationModule() {
    install(Authentication) {
        jwt {
            realm = JWTConfiguration.realm
            verifier(JWTConfiguration.verifier)
            validate { credential ->
                if (credential.payload.audience.contains(JWTConfiguration.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
