package eu.yeger.repository

import eu.yeger.model.Item

class TestItemRepository : ItemRepository {

    private val items = HashMap<String, Item>()

    override suspend fun getAll(): List<Item> = items.values.toList()

    override suspend fun getItemByName(name: String): Item? =
        items[name]

    override suspend fun hasItemWithName(name: String): Boolean =
        getItemByName(name) != null

    override suspend fun insert(item: Item) {
        items[item.name] = item
    }
}
