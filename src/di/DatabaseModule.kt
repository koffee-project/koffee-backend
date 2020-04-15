package eu.yeger.di

import eu.yeger.Arguments
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val databaseModule = module {
    single {
        KMongo
            .createClient("mongodb://${Arguments.databaseHost}:${Arguments.databasePort}")
            .coroutine
            .getDatabase(Arguments.databaseName)
    }
}
