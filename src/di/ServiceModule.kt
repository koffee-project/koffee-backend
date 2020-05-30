package eu.yeger.di

import eu.yeger.service.DefaultImageService
import eu.yeger.service.DefaultItemService
import eu.yeger.service.DefaultTransactionService
import eu.yeger.service.DefaultUserService
import eu.yeger.service.ImageService
import eu.yeger.service.ItemService
import eu.yeger.service.TransactionService
import eu.yeger.service.UserService
import org.koin.dsl.module

val serviceModule = module {
    single<UserService> {
        DefaultUserService(get(), get())
    }

    single<ItemService> {
        DefaultItemService(get())
    }

    single<TransactionService> {
        DefaultTransactionService(get(), get())
    }

    single<ImageService> {
        DefaultImageService(get(), get())
    }
}
