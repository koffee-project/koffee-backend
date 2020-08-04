package eu.yeger.koffee.model.dto

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.domain.Transaction
import eu.yeger.koffee.model.domain.TransactionList
import eu.yeger.koffee.model.domain.User
import java.util.UUID

/**
 * DTO class for creating and updating [User]s.
 *
 * @property id The id of the [User].
 * @property name The name of the [User].
 * @property isAdmin Indicates whether the [User] has admin privileges.
 * @property password The optional password of the [User].
 *
 * @author Jan Müller
 */
data class PartialUser(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isAdmin: Boolean,
    val password: String?
)

/**
 * Extension method for turning [PartialUser]s into [User]s without [Transaction]s.
 *
 * @receiver The source [PartialUser].
 *
 * @author Jan Müller
 */
fun PartialUser.asDomainUser(profileImage: ProfileImage? = null) = User(
    id = id,
    name = name,
    transactions = TransactionList(emptyList()),
    isAdmin = isAdmin,
    password = password,
    profileImage = profileImage
)
