package eu.yeger.utility

import eu.yeger.model.domain.Item
import eu.yeger.model.domain.User
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository

suspend fun UserRepository.validateUserExists(userId: String): Result<User> {
    return when (val user = getById(id = userId)) {
        null -> Result.notFound(NO_USER_WITH_THAT_ID)
        else -> Result.ok(user)
    }
}

suspend fun UserRepository.validateUserDoesNotExist(partialUser: PartialUser): Result<PartialUser> {
    return when (getById(id = partialUser.id)) {
        null -> Result.ok(partialUser)
        else -> Result.conflict(USER_WITH_THAT_ID_ALREADY_EXISTS)
    }
}

suspend fun ItemRepository.validateItemExists(itemId: String): Result<Item> {
    return when (val item = getById(id = itemId)) {
        null -> Result.notFound(NO_ITEM_WITH_THAT_ID)
        else -> Result.ok(item)
    }
}

suspend fun ItemRepository.validateItemDoesNotExist(item: Item): Result<Item> {
    return when (getById(id = item.id)) {
        null -> Result.ok(item)
        else -> Result.conflict(ITEM_WITH_THAT_ID_ALREADY_EXISTS)
    }
}