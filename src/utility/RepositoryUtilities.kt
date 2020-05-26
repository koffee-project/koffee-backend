package eu.yeger.utility

import eu.yeger.model.domain.Item
import eu.yeger.model.domain.User
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository

suspend fun UserRepository.validateUserExists(userId: String): Result<User> {
    return when (val user = getById(id = userId)) {
        null -> Result.NotFound(NO_USER_WITH_THAT_ID)
        else -> Result.OK(user)
    }
}

suspend fun UserRepository.validateUserDoesNotExist(partialUser: PartialUser): Result<PartialUser> {
    return when (getById(id = partialUser.id)) {
        null -> Result.OK(partialUser)
        else -> Result.Conflict(USER_WITH_THAT_ID_ALREADY_EXISTS)
    }
}

suspend fun ItemRepository.validateItemExists(itemId: String): Result<Item> {
    return when (val item = getById(id = itemId)) {
        null -> Result.NotFound(NO_ITEM_WITH_THAT_ID)
        else -> Result.OK(item)
    }
}

suspend fun ItemRepository.validateItemDoesNotExist(item: Item): Result<Item> {
    return when (getById(id = item.id)) {
        null -> Result.OK(item)
        else -> Result.Conflict(ITEM_WITH_THAT_ID_ALREADY_EXISTS)
    }
}
