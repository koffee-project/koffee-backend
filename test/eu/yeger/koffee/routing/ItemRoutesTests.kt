package eu.yeger.koffee.routing

import com.fasterxml.jackson.databind.SerializationFeature
import eu.yeger.koffee.authentication.authenticationModule
import eu.yeger.koffee.di.fakeRepositoryModule
import eu.yeger.koffee.di.serviceModule
import eu.yeger.koffee.initializeDefaultAdmin
import eu.yeger.koffee.utility.shouldBe
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Test
import org.koin.ktor.ext.Koin

class ItemRoutesTests {

    private val testModule: Application.() -> Unit = {
        authenticationModule()

        routing {
            route("/") {
                itemRoutes()
            }
        }

        install(Koin) {
            modules(serviceModule + fakeRepositoryModule)
        }

        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }

        initializeDefaultAdmin()
    }

    @Test
    fun `verify that GET-items does not require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Get
            uri = "/items"
        }

        // Then the request is accepted
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.OK
        call.response.content?.isNotBlank() shouldBe true
    }

    @Test
    fun `verify that GET-items-$id does not require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Get
            uri = "/items/wtr"
        }

        // Then the request is accepted
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `verify that POST-items does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/items"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `verify that PUT-items does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Put
            uri = "/items"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `verify that DELETE-items-$id does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Delete
            uri = "/items/wtr"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }
}
