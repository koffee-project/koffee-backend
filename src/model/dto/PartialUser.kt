package eu.yeger.model.dto

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.TransactionList
import eu.yeger.model.domain.User
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
fun PartialUser.asUser() = User(
    id = id,
    name = name,
    transactions = TransactionList(emptyList()),
    isAdmin = isAdmin,
    password = password
)
