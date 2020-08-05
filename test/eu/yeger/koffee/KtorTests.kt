package eu.yeger.koffee

import eu.yeger.koffee.repository.ItemRepository
import eu.yeger.koffee.repository.UserRepository
import eu.yeger.koffee.service.ItemService
import eu.yeger.koffee.service.ProfileImageService
import eu.yeger.koffee.service.TransactionService
import eu.yeger.koffee.service.UserService
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Test
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

class KtorTests {

    @Test
    fun `verify main module`() {
        withTestApplication({ mainModule() }) {
            // Check database
            application.inject<CoroutineDatabase>()

            // Check repositories
            application.inject<ItemRepository>()
            application.inject<UserRepository>()

            // Check services
            application.inject<ItemService>()
            application.inject<ProfileImageService>()
            application.inject<TransactionService>()
            application.inject<UserService>()
        }
    }
}
