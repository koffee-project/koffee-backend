package eu.yeger.model.dto

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.User

data class UserProfile(
    val id: String,
    val name: String,
    val balance: Double
)

fun User.asProfile() = UserProfile(
    id = id,
    name = name,
    balance = transactions.sumByDouble(Transaction::value)
)
