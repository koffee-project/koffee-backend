package eu.yeger.repository

import eu.yeger.model.Item

class FakeItemRepository : ItemRepository {

    private val items = HashMap<String, Item>()

    override suspend fun getAll(): List<Item> = items.values.toList()

    override suspend fun getById(id: String): Item? =
        items[id]

    override suspend fun hasItemWithId(id: String): Boolean =
        getById(id) != null

    override suspend fun insert(item: Item) {
        items[item.id] = item
    }

    override suspend fun removeById(id: String) {
        items.remove(id)
    }
}
