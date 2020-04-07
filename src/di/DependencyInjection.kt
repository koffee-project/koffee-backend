package eu.yeger.di

import io.ktor.application.Application
import io.ktor.application.install
import org.koin.ktor.ext.Koin

fun Application.installKoin() = install(Koin) {
    modules(serviceModule + repositoryModule + databaseModule)
}
