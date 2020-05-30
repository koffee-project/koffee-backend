package eu.yeger.di

import eu.yeger.repository.FakeImageRepository
import eu.yeger.repository.FakeItemRepository
import eu.yeger.repository.FakeUserRepository
import eu.yeger.repository.ImageRepository
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository
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
