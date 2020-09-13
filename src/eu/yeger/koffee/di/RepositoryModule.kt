package eu.yeger.koffee.di

import eu.yeger.koffee.repository.ItemRepository
import eu.yeger.koffee.repository.MongoItemRepository
import eu.yeger.koffee.repository.MongoUserRepository
import eu.yeger.koffee.repository.UserRepository
import org.koin.dsl.module

/**
 * The repository Koin module.
 * Uses MongoDB repositories.
 *
 * @author Jan Müller
 */
val repositoryModule = module {
    single<UserRepository> {
        MongoUserRepository(get())
    }

    single<ItemRepository> {
        MongoItemRepository(get())
    }
}
