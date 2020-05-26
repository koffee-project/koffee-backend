package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.dto.Result
import eu.yeger.model.dto.andThen
import eu.yeger.model.dto.withResult
import eu.yeger.repository.ItemRepository
import eu.yeger.utility.INVALID_ITEM_DATA
import eu.yeger.utility.ITEM_CREATED_SUCCESSFULLY
import eu.yeger.utility.ITEM_DELETED_SUCCESSFULLY
import eu.yeger.utility.ITEM_UPDATED_SUCCESSFULLY
import eu.yeger.utility.hasTwoDecimalPlaces
import eu.yeger.utility.validateItemDoesNotExist
import eu.yeger.utility.validateItemExists

class DefaultItemService(private val itemRepository: ItemRepository) : ItemService {

    override suspend fun getAllItems(): Result<List<Item>> {
        val items = itemRepository.getAll()
        return Result.ok(items)
    }

    override suspend fun getItemById(id: String): Result<Item> {
        return itemRepository
            .validateItemExists(id)
            .andThen { item -> Result.ok(item) }
    }

    override suspend fun createItem(item: Item): Result<String> {
        return itemRepository
            .validateItemDoesNotExist(item)
            .andThen { validateItem(it) }
            .withResult { itemRepository.insert(it) }
            .andThen { Result.created(ITEM_CREATED_SUCCESSFULLY) }
    }

    override suspend fun updateItem(item: Item): Result<String> {
        return itemRepository
            .validateItemExists(item.id)
            .andThen { validateItem(item) }
            .withResult { itemRepository.insert(it) }
            .andThen { Result.ok(ITEM_UPDATED_SUCCESSFULLY) }
    }

    override suspend fun deleteItemById(id: String): Result<String> {
        return itemRepository
            .validateItemExists(id)
            .withResult { item -> itemRepository.removeById(item.id) }
            .andThen { Result.ok(ITEM_DELETED_SUCCESSFULLY) }
    }

    private fun validateItem(item: Item): Result<Item> {
        return when (item.isValid()) {
            true -> Result.ok(item)
            false -> Result.unprocessableEntity(INVALID_ITEM_DATA)
        }
    }

    private fun Item.isValid(): Boolean {
        return id.isNotBlank() &&
            name.isNotBlank() &&
            (amount == null || amount >= 0) &&
            price > 0 &&
            price.hasTwoDecimalPlaces()
    }
}
