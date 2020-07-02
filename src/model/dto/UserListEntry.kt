package eu.yeger.model.dto

import eu.yeger.model.domain.User

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
 * @author Jan Müller
 */
fun User.asUserListEntry() = UserListEntry(
    id = id,
    name = name
)
