package eu.yeger.repository

import eu.yeger.model.User
import eu.yeger.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserRepository(database: CoroutineDatabase) : UserRepository {

    private val userCollection = database.getCollection<User>()

    override suspend fun getAll(): List<User> =
        userCollection.find().toList()

    override suspend fun getByName(name: String): User? =
        userCollection.findOne(filter = User::name eq name)

    override suspend fun hasUserWithName(name: String): Boolean =
        getByName(name) != null

    override suspend fun insert(user: User) {
        userCollection.upsert(id = user.name, entity = user)
    }

    override suspend fun removeByName(name: String) {
        userCollection.deleteOneById(id = name)
    }
}
