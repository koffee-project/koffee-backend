package eu.yeger.koffee.utility

import eu.yeger.koffee.model.domain.Item
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.PartialUser
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.repository.ItemRepository
import eu.yeger.koffee.repository.UserRepository

/**
 * Validates that the [User] with the given id exists in the [UserRepository].
 *
 * @receiver The [UserRepository].
 * @param userId The id of the [User].
 * @return The [Result] of the operation.
 *
 * @author Jan Müller
 */
suspend fun UserRepository.validateUserExists(userId: String): Result<User> {
    return when (val user = getById(id = userId)) {
        null -> Result.notFound(NO_USER_WITH_THAT_ID)
        else -> Result.ok(user)
    }
}

/**
 * Validates that the [User] with the given id does not exist in the [UserRepository].
 *
 * @receiver The [UserRepository].
 * @param partialUser The [PartialUser] containing the [User]'s id.
 * @return The [Result] of the operation.
 *
 * @author Jan Müller
 */
suspend fun UserRepository.validateUserDoesNotExist(partialUser: PartialUser): Result<PartialUser> {
    return when (getById(id = partialUser.id)) {
        null -> Result.ok(partialUser)
        else -> Result.conflict(USER_WITH_THAT_ID_ALREADY_EXISTS)
    }
}

/**
 * Validates that the [Item] with the given id exists in the [ItemRepository].
 *
 * @receiver The [ItemRepository].
 * @param itemId The id of the [Item].
 * @return The [Result] of the operation.
 *
 * @author Jan Müller
 */
suspend fun ItemRepository.validateItemExists(itemId: String): Result<Item> {
    return when (val item = getById(id = itemId)) {
        null -> Result.notFound(NO_ITEM_WITH_THAT_ID)
        else -> Result.ok(item)
    }
}

/**
 * Validates that the [Item] with the given id does not exist in the [ItemRepository].
 *
 * @receiver The [ItemRepository].
 * @param item The [Item] containing the id.
 * @return The [Result] of the operation.
 *
 * @author Jan Müller
 */
suspend fun ItemRepository.validateItemDoesNotExist(item: Item): Result<Item> {
    return when (getById(id = item.id)) {
        null -> Result.ok(item)
        else -> Result.conflict(ITEM_WITH_THAT_ID_ALREADY_EXISTS)
    }
}
