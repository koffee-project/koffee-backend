package eu.yeger.repository

import eu.yeger.model.domain.Item
import eu.yeger.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoItemRepository(database: CoroutineDatabase) : ItemRepository {

    private val itemCollection = database.getCollection<Item>()

    override suspend fun getAll(): List<Item> =
        itemCollection.find().toList()

    override suspend fun getById(id: String): Item? =
        itemCollection.findOneById(id)

    override suspend fun hasItemWithId(id: String): Boolean =
        getById(id) != null

    override suspend fun insert(item: Item) {
        itemCollection.upsert(entity = item)
    }

    override suspend fun removeById(id: String) {
        itemCollection.deleteOneById(id = id)
    }
}
