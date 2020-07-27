package eu.yeger.koffee.repository

import eu.yeger.koffee.model.domain.Item
import eu.yeger.koffee.utility.combineAsFilter
import eu.yeger.koffee.utility.incrementBy
import eu.yeger.koffee.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.ne

/**
 * [ItemRepository] based on MongoDB.
 *
 * @author Jan Müller
 */
class MongoItemRepository(database: CoroutineDatabase) : ItemRepository {

    private val itemCollection = database.getCollection<Item>()

    override suspend fun getAll(): List<Item> = itemCollection.find().toList()

    override suspend fun getById(id: String): Item? = itemCollection.findOneById(id)

    override suspend fun hasItemWithId(id: String): Boolean = getById(id) != null

    override suspend fun insert(item: Item) {
        itemCollection.upsert(entity = item)
    }

    override suspend fun removeById(id: String) {
        itemCollection.deleteOneById(id = id)
    }

    override suspend fun updateAmount(id: String, change: Int) {
        val filter = listOf(
            Item::id eq id,
            Item::amount ne null
        ).combineAsFilter()

        itemCollection.findOneAndUpdate(
            filter = filter,
            update = Item::amount incrementBy change
        )
    }
}
