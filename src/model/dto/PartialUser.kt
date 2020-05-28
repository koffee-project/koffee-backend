package eu.yeger.model.dto

import eu.yeger.model.domain.TransactionList
import eu.yeger.model.domain.User
import java.util.UUID

data class PartialUser(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isAdmin: Boolean,
    val password: String?
)

fun PartialUser.asUser() = User(
    id = id,
    name = name,
    transactions = TransactionList(emptyList()),
    isAdmin = isAdmin,
    password = password
)
