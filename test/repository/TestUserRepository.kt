package eu.yeger.repository

import eu.yeger.model.User

class TestUserRepository : UserRepository {

    private val users = HashMap<String, User>()

    override suspend fun getAll(): List<User> = users.values.toList()

    override suspend fun getByName(name: String): User? = users[name]

    override suspend fun hasUserWithName(name: String): Boolean =
        getByName(name) != null

    override suspend fun insert(user: User) {
        users[user.name] = user
    }
}
