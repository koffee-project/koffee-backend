package eu.yeger.repository

import eu.yeger.model.User
import eu.yeger.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.inc

class MongoUserRepository(database: CoroutineDatabase) : UserRepository {

    private val userCollection = database.getCollection<User>()

    override suspend fun getAll(): List<User> =
        userCollection.find().toList()

    override suspend fun getById(id: String): User? =
        userCollection.findOneById(id)

    override suspend fun hasUserWithId(id: String): Boolean =
        getById(id) != null

    override suspend fun insert(user: User) {
        userCollection.upsert(entity = user)
    }

    override suspend fun removeById(id: String) {
        userCollection.deleteOneById(id = id)
    }

    override suspend fun updateBalance(id: String, change: Double) {
        userCollection.findOneAndUpdate(
            filter = User::id eq id,
            update = inc(User::balance, change)
        )
    }
}
