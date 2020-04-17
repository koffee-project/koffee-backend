package eu.yeger

import com.fasterxml.jackson.databind.SerializationFeature
import eu.yeger.authentication.withHashedPassword
import eu.yeger.di.databaseModule
import eu.yeger.di.repositoryModule
import eu.yeger.di.serviceModule
import eu.yeger.model.domain.User
import eu.yeger.repository.UserRepository
import eu.yeger.utility.loadDockerSecrets
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.path
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.mainModule() {
    install(Koin) {
        modules(serviceModule + repositoryModule + databaseModule)
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    initializeDefaultAdmin()
}

fun Application.initializeDefaultAdmin() {
    val userRepository: UserRepository by inject()

    val defaultAdminSecrets = loadDockerSecrets(fileName = Arguments.defaultAdminSecret)

    val defaultAdmin = User(
        id = defaultAdminSecrets["ID"] ?: "admin",
        name = defaultAdminSecrets["NAME"] ?: "admin",
        transactions = emptyList(),
        isAdmin = true,
        password = defaultAdminSecrets["PASSWORD"] ?: "admin"
    ).withHashedPassword()

    runBlocking {
        userRepository.insert(defaultAdmin)
    }
}
