package eu.yeger.repository

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.User
import eu.yeger.utility.combineAsUpdate
import eu.yeger.utility.push
import eu.yeger.utility.to
import eu.yeger.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserRepository(database: CoroutineDatabase) : UserRepository {

    private val userCollection = database.getCollection<User>()

    override suspend fun getAll(): List<User> = userCollection.find().toList()

    override suspend fun getById(id: String): User? = userCollection.findOneById(id)

    override suspend fun hasUserWithId(id: String): Boolean = getById(id) != null

    override suspend fun insert(user: User) {
        userCollection.upsert(entity = user)
    }

    override suspend fun update(id: String, name: String, isAdmin: Boolean, password: String?) {
        val update = listOf(
            User::name to name,
            User::isAdmin to isAdmin,
            User::password to password
        ).combineAsUpdate()

        userCollection.findOneAndUpdate(
            filter = User::id eq id,
            update = update
        )
    }

    override suspend fun removeById(id: String) {
        userCollection.deleteOneById(id = id)
    }

    override suspend fun addTransaction(id: String, transaction: Transaction) {
        userCollection.findOneAndUpdate(
            filter = User::id eq id,
            update = User::transactions push transaction
        )
    }
}
