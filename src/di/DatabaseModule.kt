package eu.yeger.di

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val databaseModule = module {
    single {
        KMongo
            .createClient("mongodb://mongodb:27017")
            .coroutine
            .getDatabase("koffee-backend")
    }
}
