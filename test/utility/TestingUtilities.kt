package eu.yeger.utility

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.model.domain.Item
import eu.yeger.model.domain.User
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Purchase
import eu.yeger.model.dto.asUser
import io.ktor.server.testing.TestApplicationRequest

val testItem = Item(
    id = "water",
    name = "Water",
    amount = 42,
    price = 0.5
)

val testPartialUser = PartialUser(
    id = "userName",
    name = "UserName",
    isAdmin = false,
    password = "testPassword"
)

val testUser = testPartialUser.asUser()

val testFunding = Funding(
    amount = 42.0
)

val testPurchase = Purchase(
    itemId = testItem.id,
    amount = 42
)

fun TestApplicationRequest.addJWTHeader(user: User) {
    val token = JWTConfiguration.makeToken(user)
    addHeader("Authorization", "Bearer $token")
}
