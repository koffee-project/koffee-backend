package eu.yeger.utility

import eu.yeger.model.domain.Item
import eu.yeger.model.domain.ProfileImage
import eu.yeger.model.domain.User
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Result
import eu.yeger.repository.ImageRepository
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository

/**
 * Validates that the [User] with the given id exists.
 *
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
 * Validates that the [User] with the given id does not exist.
 *
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
 * Validates that the [Item] with the given id exists.
 *
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
 * Validates that the [Item] with the given id does not exist.
 *
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

/**
 * Validates that the [ProfileImage] with the given id of a [User] exists.
 *
 * @param userId The id of the [User].
 * @return The [Result] of the operation.
 *
 * @author Jan Müller
 */
suspend fun ImageRepository.validateProfileImageExists(userId: String): Result<ProfileImage> {
    return when (val profileImage = getByUserId(id = userId)) {
        null -> Result.notFound(NO_IMAGE_FOR_THAT_USER_ID)
        else -> Result.ok(profileImage)
    }
}
