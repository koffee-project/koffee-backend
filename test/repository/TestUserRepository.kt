package eu.yeger.repository

import eu.yeger.model.User

class TestUserRepository : UserRepository {

    private val users = HashMap<String, User>()

    override suspend fun getAll(): List<User> = users.values.toList()

    override suspend fun getById(id: String): User? = users[id]

    override suspend fun hasUserWithId(id: String): Boolean =
        getById(id) != null

    override suspend fun insert(user: User) {
        users[user.id] = user
    }

    override suspend fun removeById(id: String) {
        users.remove(id)
    }
}
