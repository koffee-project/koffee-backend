package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.dto.Result
import eu.yeger.repository.ItemRepository
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
            true -> Result.Conflict("Item with that id already exists")
            false -> item.validated {
                itemRepository.insert(item)
                Result.Created("Created $item")
            }
        }
    }

    override suspend fun updateItem(item: Item): Result<String> {
        return when (itemRepository.hasItemWithId(id = item.id)) {
            true -> item.validated {
                itemRepository.insert(item)
                Result.OK("Updated $item")
            }
            false -> Result.Conflict("Item with that id does not exist")
        }
    }

    override suspend fun deleteItemById(id: String): Result<String> {
        return when (itemRepository.hasItemWithId(id = id)) {
            true -> {
                itemRepository.removeById(id)
                Result.OK("Deleted $id")
            }
            false -> Result.NotFound("Item with that id does not exist")
        }
    }

    private inline fun Item.validated(block: () -> Result<String>): Result<String> =
        when (this.isValid()) {
            true -> block()
            false -> Result.UnprocessableEntity("$this is invalid")
        }

    private fun Item.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            amount >= 0 &&
            price > 0 &&
            price.hasTwoDecimalPlaces()
}
