package eu.yeger.koffee.repository

import eu.yeger.koffee.model.domain.Item

class FakeItemRepository : ItemRepository {

    private val items = HashMap<String, Item>()

    override suspend fun getAll(): List<Item> = items.values.toList()

    override suspend fun getById(id: String): Item? = items[id]

    override suspend fun hasItemWithId(id: String): Boolean = getById(id) != null

    override suspend fun insert(item: Item) {
        items[item.id] = item
    }

    override suspend fun removeById(id: String) {
        items.remove(id)
    }

    override suspend fun updateAmount(id: String, change: Int) {
        items[id]?.let { oldItem ->
            oldItem.amount?.let { oldAmount ->
                items[id] = oldItem.copy(amount = oldAmount + change)
            }
        }
    }
}
