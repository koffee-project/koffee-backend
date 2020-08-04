package eu.yeger.koffee.repository

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.domain.Transaction
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.utility.combineAsUpdate
import eu.yeger.koffee.utility.push
import eu.yeger.koffee.utility.to
import eu.yeger.koffee.utility.updateById
import eu.yeger.koffee.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import org.litote.kmongo.unset

/**
 * [UserRepository] based on MongoDB.
 *
 * @author Jan Müller
 */
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

    override suspend fun addProfileImage(id: String, profileImage: ProfileImage) {
        userCollection.updateById(id, setValue(User::profileImage, profileImage))
    }

    override suspend fun removeProfileImage(id: String) {
        userCollection.updateById(id, unset(User::profileImage))
    }
}
