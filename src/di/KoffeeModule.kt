package eu.yeger.di

import eu.yeger.repository.MongoUserRepository
import eu.yeger.repository.UserRepository
import eu.yeger.service.DefaultUserService
import eu.yeger.service.UserService
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val koffeeModule = module {

    single {
        KMongo
            .createClient("mongodb://mongodb:27017")
            .coroutine
            .getDatabase("koffee-backend")
    }

    single<UserRepository> {
        MongoUserRepository(get())
    }

    single<UserService> {
        DefaultUserService(get())
    }
}
