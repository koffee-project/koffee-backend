package eu.yeger.koffee.utility

import eu.yeger.koffee.authentication.JWTConfiguration
import eu.yeger.koffee.model.domain.Item
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Funding
import eu.yeger.koffee.model.dto.PartialUser
import eu.yeger.koffee.model.dto.Purchase
import eu.yeger.koffee.model.dto.asDomainUser
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

val testUser = testPartialUser.asDomainUser()

val testFunding = Funding(
    amount = 42.0
)

val testPurchase = Purchase(
    itemId = testItem.id,
    amount = 42
)

fun TestApplicationRequest.addJWTHeader(user: User) {
    val token = JWTConfiguration.makeToken(user)!!.token
    addHeader("Authorization", "Bearer $token")
}
