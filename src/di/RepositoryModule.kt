package eu.yeger.di

import eu.yeger.repository.ImageRepository
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.MongoImageRepository
import eu.yeger.repository.MongoItemRepository
import eu.yeger.repository.MongoUserRepository
import eu.yeger.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> {
        MongoUserRepository(get())
    }

    single<ItemRepository> {
        MongoItemRepository(get())
    }

    single<ImageRepository> {
        MongoImageRepository(get())
    }
}
