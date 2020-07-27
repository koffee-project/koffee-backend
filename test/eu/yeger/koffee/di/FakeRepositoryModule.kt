package eu.yeger.koffee.di

import eu.yeger.koffee.repository.FakeImageRepository
import eu.yeger.koffee.repository.FakeItemRepository
import eu.yeger.koffee.repository.FakeUserRepository
import eu.yeger.koffee.repository.ImageRepository
import eu.yeger.koffee.repository.ItemRepository
import eu.yeger.koffee.repository.UserRepository
import org.koin.dsl.module

val fakeRepositoryModule = module {
    single<UserRepository> {
        FakeUserRepository()
    }

    single<ItemRepository> {
        FakeItemRepository()
    }

    single<ImageRepository> {
        FakeImageRepository()
    }
}
