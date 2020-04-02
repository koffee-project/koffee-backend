package eu.yeger.di

import eu.yeger.service.DefaultItemService
import eu.yeger.service.DefaultUserService
import eu.yeger.service.ItemService
import eu.yeger.service.UserService
import org.koin.dsl.module

val serviceModule = module {
    single<UserService> {
        DefaultUserService(get())
    }

    single<ItemService> {
        DefaultItemService(get())
    }
}
