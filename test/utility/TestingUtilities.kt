package eu.yeger.utility

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.model.Item
import eu.yeger.model.User
import io.ktor.server.testing.TestApplicationRequest

val testItem = Item(
    id = "water",
    name = "Water",
    amount = 42,
    price = 0.5
)

val testUser = User(
    id = "userName",
    name = "UserName",
    balance = 0.0,
    isAdmin = true,
    password = "testPassword"
)

fun TestApplicationRequest.addTestUserJWTHeader() = addJWTHeader(testUser)

fun TestApplicationRequest.addJWTHeader(user: User) {
    val token = JWTConfiguration.makeToken(user)
    addHeader("Authorization", "Bearer $token")
}
