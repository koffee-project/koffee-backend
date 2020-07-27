package eu.yeger.koffee.model.dto

import eu.yeger.koffee.model.domain.User

/**
 * DTO class for metadata of [User]s.
 *
 * @property id The id of the [User].
 * @property name The name of the [User].
 *
 * @author Jan Müller
 */
data class UserListEntry(
    val id: String,
    val name: String
)

/**
 * Extension method for turning a [User] into a [UserListEntry] by extracting its metadata.
 *
 * @receiver The source [User].
 *
 * @author Jan Müller
 */
fun User.asUserListEntry() = UserListEntry(
    id = id,
    name = name
)
