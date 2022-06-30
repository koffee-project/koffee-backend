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

class UserRoutesTests {

    private val testModule: Application.() -> Unit = {
        authenticationModule()

        routing {
            route("/") {
                userRoutes()
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
    fun `verify that GET-users does not require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Get
            uri = "/users"
        }

        // Then the request is accepted
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.OK
        call.response.content?.isNotBlank() shouldBe true
    }

    @Test
    fun `verify that GET-users-$id does not require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Get
            uri = "/users/admin"
        }

        // Then the request is accepted
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.OK
        call.response.content?.isNotBlank() shouldBe true
    }

    @Test
    fun `verify that POST-users-$id-purchases does not require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/users/admin/purchases"
        }

        // Then the request is accepted
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.UnsupportedMediaType
    }

    @Test
    fun `verify that POST-users-$id-purchases-refund does not require authentication`() =
        withTestApplication(testModule) {
            // When route is accessed
            val call = handleRequest {
                method = HttpMethod.Post
                uri = "/users/admin/purchases/refund"
            }

            // Then the request is accepted
            call.requestHandled shouldBe true
            call.response.status() shouldBe HttpStatusCode.Conflict
        }

    @Test
    fun `verify that POST-users does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/users"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `verify that PUT-users does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Put
            uri = "/users"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `verify that DELETE-users-$id does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Delete
            uri = "/users/admin"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `verify that POST-users-$id-funding does require authentication`() = withTestApplication(testModule) {
        // When route is accessed
        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/users/admin/funding"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }
}
