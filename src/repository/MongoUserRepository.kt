package eu.yeger.repository

import eu.yeger.model.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserRepository(database: CoroutineDatabase) : UserRepository {

    private val userCollection = database.getCollection<User>()

    override suspend fun getAll(): List<User> =
        userCollection.find().toList()

    override suspend fun getByName(name: String): User? =
        userCollection.findOne(User::name eq name)

    override suspend fun insert(user: User) {
        userCollection.insertOne(user)
    }
}
