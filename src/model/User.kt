package eu.yeger.model

import io.ktor.auth.Principal

data class User(
    override val id: String,
    val name: String,
    val balance: Double,
    val isAdmin: Boolean,
    val password: String?
) : Principal, Entity

val User.profile: Profile
    get() = Profile(
        id = this.id,
        name = this.name,
        balance = this.balance
    )
