package eu.yeger.service

import eu.yeger.model.User
import eu.yeger.repository.UserRepository

class TestUserRepository : UserRepository {

    private val users = HashMap<String, User>()

    override suspend fun getAll(): List<User> = users.values.toList()

    override suspend fun getByName(name: String): User? = users[name]

    override suspend fun insert(user: User) {
        users[user.name] = user
    }
}
