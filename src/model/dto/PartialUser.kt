package eu.yeger.model.dto

import eu.yeger.model.domain.User

data class PartialUser(
    val id: String,
    val name: String,
    val isAdmin: Boolean,
    val password: String?
)

fun PartialUser.asUser() = User(
    id = id,
    name = name,
    transactions = emptyList(),
    isAdmin = isAdmin,
    password = password
)
