package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.dto.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.utility.INVALID_ITEM_DATA
import eu.yeger.utility.ITEM_CREATED_SUCCESSFULLY
import eu.yeger.utility.ITEM_DELETED_SUCCESSFULLY
import eu.yeger.utility.ITEM_UPDATED_SUCCESSFULLY
import eu.yeger.utility.ITEM_WITH_THAT_ID_ALREADY_EXISTS
import eu.yeger.utility.NO_ITEM_WITH_THAT_ID
import eu.yeger.utility.hasTwoDecimalPlaces

class DefaultItemService(private val itemRepository: ItemRepository) : ItemService {

    override suspend fun getAllItems(): Result<List<Item>> {
        val items = itemRepository.getAll()
        return Result.OK(items)
    }

    override suspend fun getItemById(id: String): Result<Item?> {
        return itemRepository.getById(id = id).let { item ->
            when (item) {
                null -> Result.NotFound(null)
                else -> Result.OK(item)
            }
        }
    }

    override suspend fun createItem(item: Item): Result<String> {
        return when (itemRepository.hasItemWithId(id = item.id)) {
            true -> Result.Conflict(ITEM_WITH_THAT_ID_ALREADY_EXISTS)
            false -> item.validated {
                itemRepository.insert(item)
                Result.Created(ITEM_CREATED_SUCCESSFULLY)
            }
        }
    }

    override suspend fun updateItem(item: Item): Result<String> {
        return when (itemRepository.hasItemWithId(id = item.id)) {
            true -> item.validated {
                itemRepository.insert(item)
                Result.OK(ITEM_UPDATED_SUCCESSFULLY)
            }
            false -> Result.NotFound(NO_ITEM_WITH_THAT_ID)
        }
    }

    override suspend fun deleteItemById(id: String): Result<String> {
        return when (itemRepository.hasItemWithId(id = id)) {
            true -> {
                itemRepository.removeById(id)
                Result.OK(ITEM_DELETED_SUCCESSFULLY)
            }
            false -> Result.NotFound(NO_ITEM_WITH_THAT_ID)
        }
    }

    private inline fun Item.validated(block: () -> Result<String>): Result<String> =
        when (this.isValid()) {
            true -> block()
            false -> Result.UnprocessableEntity(INVALID_ITEM_DATA)
        }

    private fun Item.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            (amount == null || amount >= 0) &&
            price > 0 &&
            price.hasTwoDecimalPlaces()
}
