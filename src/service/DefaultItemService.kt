package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.model.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.utility.hasTwoDecimalPlaces
import io.ktor.http.HttpStatusCode

class DefaultItemService(private val itemRepository: ItemRepository) : ItemService {

    override suspend fun getAllItems(): Result<List<Item>> =
        Result(status = HttpStatusCode.OK, data = itemRepository.getAll())

    override suspend fun getItemById(id: String): Result<Item?> =
        itemRepository.getById(id = id).let { item ->
            Result(status = if (item == null) HttpStatusCode.NotFound else HttpStatusCode.OK, data = item)
        }

    override suspend fun createItem(item: Item): Result<String> =
        when (itemRepository.hasItemWithId(id = item.id)) {
            true -> Result(status = HttpStatusCode.Conflict, data = "Item with that id already exists")
            false -> validated(item) {
                itemRepository.insert(item)
                Result(HttpStatusCode.Created, data = "Created $item")
            }
        }

    override suspend fun updateItem(item: Item): Result<String> =
        when (itemRepository.hasItemWithId(id = item.id)) {
            true -> validated(item) {
                itemRepository.insert(item)
                Result(status = HttpStatusCode.OK, data = "Updated $item")
            }
            false -> Result(status = HttpStatusCode.Conflict, data = "Item with that id does not exist")
        }

    override suspend fun deleteItemById(id: String): Result<String> =
        when (itemRepository.hasItemWithId(id = id)) {
            true -> {
                itemRepository.removeById(id)
                Result(status = HttpStatusCode.OK, data = "Deleted $id")
            }
            false -> Result(status = HttpStatusCode.NotFound, data = "Item with that id does not exist")
        }

    private inline fun validated(item: Item, block: () -> Result<String>): Result<String> =
        when (item.isValid()) {
            true -> block()
            false -> Result(status = HttpStatusCode.UnprocessableEntity, data = "$item is invalid")
        }

    private fun Item.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            amount >= 0 &&
            price > 0 &&
            price.hasTwoDecimalPlaces()
}
