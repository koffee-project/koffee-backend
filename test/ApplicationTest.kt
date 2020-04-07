package eu.yeger

import eu.yeger.utility.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class ApplicationTest {

    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldBe "Hello World!"
            }
        }
    }

    @Test
    fun testClientMock() {
        runBlocking {
            val client = HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        when (request.url.fullPath) {
                            "/" -> respond(
                                ByteReadChannel(byteArrayOf(1, 2, 3)),
                                headers = headersOf("X-MyHeader", "MyValue")
                            )
                            else -> respond("Not Found ${request.url.encodedPath}", HttpStatusCode.NotFound)
                        }
                    }
                }
                expectSuccess = false
            }
            client.get<ByteArray>("/").toList() shouldBe byteArrayOf(1, 2, 3).toList()
            client.request<HttpResponse>("/") {}.headers["X-MyHeader"] shouldBe "MyValue"
            client.get<String>("/other/path") shouldBe "Not Found other/path"
        }
    }
}
