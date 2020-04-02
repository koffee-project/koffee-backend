package eu.yeger.repository

import eu.yeger.model.Item
import eu.yeger.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoItemRepository(database: CoroutineDatabase) : ItemRepository {

    private val itemCollection = database.getCollection<Item>()

    override suspend fun getAll(): List<Item> =
        itemCollection.find().toList()

    override suspend fun hasItemWithName(name: String): Boolean =
        itemCollection.findOne(Item::name eq name) != null

    override suspend fun insert(item: Item) {
        itemCollection.upsert(item.name, item)
    }
}
