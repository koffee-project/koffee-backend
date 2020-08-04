package eu.yeger.koffee.model.domain

import io.ktor.auth.Principal

/**
 * [Entity] class representing [User]s.
 *
 * @property id The id of the [User].
 * @property name The name of the [User].
 * @property transactions The user's list of [Transaction]s.
 * @property isAdmin Indicates whether the [User] has admin privileges.
 * @property password The optional password of the [User].
 *
 * @author Jan Müller
 */
data class User(
    override val id: String,
    val name: String,
    val transactions: TransactionList,
    val isAdmin: Boolean,
    val password: String?,
    val profileImage: ProfileImage?
) : Principal, Entity
