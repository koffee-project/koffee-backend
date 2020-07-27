package eu.yeger.koffee

import com.fasterxml.jackson.databind.SerializationFeature
import eu.yeger.koffee.authentication.withHashedPassword
import eu.yeger.koffee.di.databaseModule
import eu.yeger.koffee.di.repositoryModule
import eu.yeger.koffee.di.serviceModule
import eu.yeger.koffee.model.domain.TransactionList
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.repository.UserRepository
import eu.yeger.koffee.utility.loadDockerSecrets
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

/**
 * Starts the engine for this server.
 *
 * @param args The arguments for this server.
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Defines the main application module for this server.
 * Installs and configures Koin, CallLogging and ContentNegotiation.
 * Also initializes the default admin.
 *
 * @receiver The target application.
 *
 * @author Jan Müller
 */
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

/**
 * Initializes the default admin using the respective Docker secret.
 *
 * @receiver The application providing the Koin context.
 *
 * @author Jan Müller
 */
fun Application.initializeDefaultAdmin() {
    val userRepository: UserRepository by inject()

    val defaultAdminSecrets = loadDockerSecrets(fileName = Arguments.koffeeSecret)

    val defaultAdmin = User(
        id = defaultAdminSecrets["ID"] ?: "admin",
        name = defaultAdminSecrets["NAME"] ?: "admin",
        transactions = TransactionList(emptyList()),
        isAdmin = true,
        password = defaultAdminSecrets["PASSWORD"] ?: "admin"
    ).withHashedPassword()

    runBlocking {
        userRepository.insert(defaultAdmin)
    }
}
