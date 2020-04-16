package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.model.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.utility.hasTwoDecimalPlaces

class DefaultItemService(private val itemRepository: ItemRepository) : ItemService {

    override suspend fun getAllItems(): Result<List<Item>> {
        val items = itemRepository.getAll()
        return Result.OK(items)
    }

    override suspend fun getItemById(id: String): Result<Item?> =
        itemRepository.getById(id = id).let { item ->
            when (item) {
                null -> Result.NotFound(null)
                else -> Result.OK(item)
            }
        }

    override suspend fun createItem(item: Item): Result<String> =
        when (itemRepository.hasItemWithId(id = item.id)) {
            true -> Result.Conflict("Item with that id already exists")
            false -> validated(item) {
                itemRepository.insert(item)
                Result.Created("Created $item")
            }
        }

    override suspend fun updateItem(item: Item): Result<String> =
        when (itemRepository.hasItemWithId(id = item.id)) {
            true -> validated(item) {
                itemRepository.insert(item)
                Result.OK("Updated $item")
            }
            false -> Result.Conflict("Item with that id does not exist")
        }

    override suspend fun deleteItemById(id: String): Result<String> =
        when (itemRepository.hasItemWithId(id = id)) {
            true -> {
                itemRepository.removeById(id)
                Result.OK("Deleted $id")
            }
            false -> Result.NotFound("Item with that id does not exist")
        }

    private inline fun validated(item: Item, block: () -> Result<String>): Result<String> =
        when (item.isValid()) {
            true -> block()
            false -> Result.UnprocessableEntity("$item is invalid")
        }

    private fun Item.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            amount >= 0 &&
            price > 0 &&
            price.hasTwoDecimalPlaces()
}
