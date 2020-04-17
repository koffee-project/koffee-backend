package eu.yeger.model.dto

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.User

data class UserProfileDTO(
    val id: String,
    val name: String,
    val transactions: List<Transaction>
)

fun User.toProfile() = UserProfileDTO(
    id = id,
    name = name,
    transactions = transactions
)
