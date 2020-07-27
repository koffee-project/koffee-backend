package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.Item
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.andThen
import eu.yeger.koffee.model.dto.withResult
import eu.yeger.koffee.repository.ItemRepository
import eu.yeger.koffee.utility.INVALID_ITEM_DATA
import eu.yeger.koffee.utility.ITEM_DELETED_SUCCESSFULLY
import eu.yeger.koffee.utility.ITEM_UPDATED_SUCCESSFULLY
import eu.yeger.koffee.utility.hasTwoDecimalPlaces
import eu.yeger.koffee.utility.validateItemDoesNotExist
import eu.yeger.koffee.utility.validateItemExists

/**
 * Default [ItemService] implementation.
 *
 * @author Jan Müller
 */
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
            .andThen { Result.created(item.id) }
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
