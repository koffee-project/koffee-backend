package eu.yeger.authentication

import com.fasterxml.jackson.databind.SerializationFeature
import eu.yeger.di.fakeRepositoryModule
import eu.yeger.di.serviceModule
import eu.yeger.routing.installRouting
import eu.yeger.utility.addTestUserJWTHeader
import eu.yeger.utility.shouldBe
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import org.koin.ktor.ext.Koin

class AuthenticationTests {

    private val testModule: Application.() -> Unit = {
        installAuthentication()

        installRouting()

        install(Koin) {
            modules(serviceModule + fakeRepositoryModule)
        }

        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
    }

    @Test
    fun `verify that login works`() = withTestApplication(testModule) {
        // TODO replace this by actual default admin implementation
        handleRequest {
            method = HttpMethod.Post
            uri = "/admin"
        }

        // When login is requested with correct credentials
        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/login"
            addHeader("Content-Type", "application/json")
            setBody(
                """
                    {
                        "id": "admin",
                        "password": "admin"
                    }
                """.trimIndent()
            )
        }

        // Then a token is returned
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.OK
        call.response.content?.isNotBlank() shouldBe true
    }

    @Test
    fun `verify that login does not work with incorrect credentials`() = withTestApplication(testModule) {
        // TODO replace this by actual default admin implementation
        handleRequest {
            method = HttpMethod.Post
            uri = "/admin"
        }

        // When login is requested with correct credentials
        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/login"
            addHeader("Content-Type", "application/json")
            setBody(
                """
                    {
                        "id": "ad",
                        "password": "min"
                    }
                """.trimIndent()
            )
        }

        // Then no token is returned
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
        call.response.content?.isNotBlank() shouldBe true
    }

    @Test
    fun `verify that login does not work for non-admins`() = withTestApplication(testModule) {
        // When login is requested for user without admin privileges
        handleRequest {
            method = HttpMethod.Post
            uri = "/users"
            addHeader("Content-Type", "application/json")
            setBody(
                """
                    {
                        "id": "userId",
                        "name": "UserName",
                        "balance": 0.0,
                        "isAdmin": false,
                        "password": "userPassword"
                    }
                """.trimIndent()
            )
            addTestUserJWTHeader()
        }

        val call = handleRequest {
            method = HttpMethod.Post
            uri = "/login"
            addHeader("Content-Type", "application/json")
            setBody(
                """
                    {
                        "id": "userId",
                        "password": "userPassword"
                    }
                """.trimIndent()
            )
        }

        // Then no token is returned
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
        call.response.content?.isNotBlank() shouldBe true
    }

    @Test
    fun `verify that secured route cannot be accessed without token`() = withTestApplication(testModule) {
        // When user without token accesses secured route
        val call = handleRequest {
            method = HttpMethod.Delete
            uri = "/users/test"
        }

        // Then the request is denied
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `verify that secured route can be accessed with token`() = withTestApplication(testModule) {
        // When user with token accesses secured route
        val call = handleRequest {
            method = HttpMethod.Delete
            uri = "/users/test"
            addTestUserJWTHeader()
        }

        // Then the request is accepted
        call.requestHandled shouldBe true
        call.response.status() shouldBe HttpStatusCode.NotFound
    }
}
