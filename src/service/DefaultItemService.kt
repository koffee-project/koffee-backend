package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.model.Result
import eu.yeger.repository.ItemRepository
import io.ktor.http.HttpStatusCode

class DefaultItemService(private val itemRepository: ItemRepository) : ItemService {

    override suspend fun getAllItems(): Result<List<Item>> =
        Result(
            status = HttpStatusCode.OK,
            data = itemRepository.getAll()
        )

    override suspend fun getItemByName(name: String): Result<Item?> =
        itemRepository.getItemByName(name = name).let { item ->
            Result(
                status = if (item == null) HttpStatusCode.NotFound else HttpStatusCode.OK,
                data = item
            )
        }

    override suspend fun createItem(item: Item): Result<String> =
        when (itemRepository.hasItemWithName(name = item.name)) {
            true -> Result(
                status = HttpStatusCode.Conflict,
                data = "Item with that name already exists"
            )
            false -> {
                itemRepository.insert(item)
                Result(
                    HttpStatusCode.Created,
                    data = "Created $item"
                )
            }
        }

    override suspend fun updateItem(item: Item): Result<String> =
        when (itemRepository.hasItemWithName(name = item.name)) {
            true -> {
                itemRepository.insert(item)
                Result(
                    status = HttpStatusCode.OK,
                    data = "Updated $item"
                )
            }
            false -> Result(
                status = HttpStatusCode.Conflict,
                data = "Item with that name does not exist"
            )
        }

    override suspend fun deleteItemByName(name: String): Result<String> =
        when (itemRepository.hasItemWithName(name = name)) {
            true -> {
                itemRepository.removeByName(name)
                Result(
                    status = HttpStatusCode.OK,
                    data = "Deleted $name"
                )
            }
            false -> Result(
                status = HttpStatusCode.NotFound,
                data = "Item with that name does not exist"
            )
        }
}
