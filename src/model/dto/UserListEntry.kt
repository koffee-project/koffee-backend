package eu.yeger.model.dto

import eu.yeger.model.domain.User

data class UserListEntry(
    val id: String,
    val name: String
)

fun User.asUserListEntry() = UserListEntry(
    id = id,
    name = name
)
