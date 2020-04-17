package eu.yeger.model.domain

import io.ktor.auth.Principal

data class User(
    override val id: String,
    val name: String,
    val transactions: List<Transaction>,
    val isAdmin: Boolean,
    val password: String?
) : Principal, Entity
