package eu.yeger.utility

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.model.domain.Item
import eu.yeger.model.domain.User
import eu.yeger.model.dto.UserCreationRequest
import eu.yeger.model.dto.asUser
import io.ktor.server.testing.TestApplicationRequest

val testItem = Item(
    id = "water",
    name = "Water",
    amount = 42,
    price = 0.5
)

val testUserCreationRequest = UserCreationRequest(
    id = "userName",
    name = "UserName",
    isAdmin = false,
    password = "testPassword"
)

val testUser = testUserCreationRequest.asUser()

fun TestApplicationRequest.addJWTHeader(user: User) {
    val token = JWTConfiguration.makeToken(user)
    addHeader("Authorization", "Bearer $token")
}
