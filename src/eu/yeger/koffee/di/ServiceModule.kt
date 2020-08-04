package eu.yeger.koffee.di

import eu.yeger.koffee.service.DefaultItemService
import eu.yeger.koffee.service.DefaultProfileImageService
import eu.yeger.koffee.service.DefaultTransactionService
import eu.yeger.koffee.service.DefaultUserService
import eu.yeger.koffee.service.ItemService
import eu.yeger.koffee.service.ProfileImageService
import eu.yeger.koffee.service.TransactionService
import eu.yeger.koffee.service.UserService
import org.koin.dsl.module

/**
 * The service Koin module.
 *
 * @author Jan Müller
 */
val serviceModule = module {
    single<UserService> {
        DefaultUserService(get())
    }

    single<ItemService> {
        DefaultItemService(get())
    }

    single<TransactionService> {
        DefaultTransactionService(get(), get())
    }

    single<ProfileImageService> {
        DefaultProfileImageService(get())
    }
}
